package com.wiinvent.checkinservice.service.impl;

import com.wiinvent.checkinservice.dto.request.DeductPointsRequest;
import com.wiinvent.checkinservice.dto.response.DeductPointsResponse;
import com.wiinvent.checkinservice.entity.Wallet;
import com.wiinvent.checkinservice.entity.WalletTransaction;
import com.wiinvent.checkinservice.entity.enums.TransactionType;
import com.wiinvent.checkinservice.exception.AppException;
import com.wiinvent.checkinservice.exception.ErrorCode;
import com.wiinvent.checkinservice.exception.ResourceNotFoundException;
import com.wiinvent.checkinservice.repository.WalletRepository;
import com.wiinvent.checkinservice.repository.WalletTransactionRepository;
import com.wiinvent.checkinservice.service.CacheService;
import com.wiinvent.checkinservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final WalletRepository walletRepo;
    private final WalletTransactionRepository txnRepo;
    private final CacheService cacheService;

    @Override
    public DeductPointsResponse simulatePayment(DeductPointsRequest request) {
        Long userId = request.getUserId();
        long amount = request.getAmount();
        String txnRefCode = request.getTxnRefCode();

        String lockKey = "wallet:deduct:" + userId;
        RLock lock = null;

        try {
            // Acquire Redis distributed lock for concurrency processing
            lock = cacheService.tryLock(lockKey, 3000, 10_000, TimeUnit.MILLISECONDS);
            if (lock == null || !lock.isLocked()) {
                throw new AppException(ErrorCode.UNKNOWN_EXCEPTION, "System busy, please retry later");
            }

            Wallet wallet = walletRepo.findByUser_UserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

            // Check idempotency
            if (txnRepo.existsByRefCode(txnRefCode)) {
                return DeductPointsResponse.builder()
                        .success(true)
                        .balance(wallet.getBalance())
                        .txnRefCode(txnRefCode)
                        .message("Idempotent request, already processed")
                        .build();
            }

            // Check balance
            if (wallet.getBalance() < amount) {
                return DeductPointsResponse.builder()
                        .success(false)
                        .balance(wallet.getBalance())
                        .txnRefCode(txnRefCode)
                        .message("Insufficient balance")
                        .build();
            }

            // save WalletTransaction
            WalletTransaction txn = WalletTransaction.builder()
                    .wallet(wallet)
                    .amount(amount)
                    .txnType(TransactionType.PAYMENT)
                    .refCode(txnRefCode)
                    .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                    .build();
            txnRepo.save(txn);

            // Point deduct
            wallet.setBalance(wallet.getBalance() - amount);
            wallet.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
            walletRepo.save(wallet);

            return DeductPointsResponse.builder()
                    .success(true)
                    .balance(wallet.getBalance())
                    .txnRefCode(txnRefCode)
                    .message("Payment successfully")
                    .build();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(e.getMessage(), e);
            throw new AppException(ErrorCode.UNKNOWN_EXCEPTION, "Error occurs");
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                try {
                    lock.unlock();
                } catch (Exception ex) {
                    log.warn("Unable to unlock", ex);
                }
            }
        }
    }
}
