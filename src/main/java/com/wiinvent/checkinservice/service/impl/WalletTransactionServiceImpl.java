package com.wiinvent.checkinservice.service.impl;

import com.wiinvent.checkinservice.dto.response.WalletTransactionResponse;
import com.wiinvent.checkinservice.entity.User;
import com.wiinvent.checkinservice.entity.WalletTransaction;
import com.wiinvent.checkinservice.entity.enums.TransactionType;
import com.wiinvent.checkinservice.exception.ResourceNotFoundException;
import com.wiinvent.checkinservice.repository.UserRepository;
import com.wiinvent.checkinservice.repository.WalletTransactionRepository;
import com.wiinvent.checkinservice.service.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class WalletTransactionServiceImpl implements WalletTransactionService {

    private final WalletTransactionRepository repository;
    private final UserRepository userRepository;

    @Override
    public Page<WalletTransactionResponse> getUserPointRewardHistory(String username, ZoneId userZone, int page, int size, String sortDirection) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Pageable pageable = getPageable(sortDirection, page, size);

        Page<WalletTransaction> walletTransactions = repository.findByWalletUserUserIdAndTxnType(user.getUserId(), TransactionType.REWARD, pageable);
        return getResponsePage(userZone, walletTransactions);
    }

    @Override
    public Page<WalletTransactionResponse> getUserTransactions(String username, ZoneId userZone, int page, int size, String sortDirection) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Pageable pageable = getPageable(sortDirection, page, size);
        Page<WalletTransaction> walletTransactions = repository.findByWalletUserUserId(user.getUserId(), pageable);

        return getResponsePage(userZone, walletTransactions);
    }

    private static Page<WalletTransactionResponse> getResponsePage(ZoneId userZone, Page<WalletTransaction> walletTransactions) {
        return walletTransactions.map(tx -> WalletTransactionResponse.builder()
                .amount(tx.getAmount())
                .refCode(tx.getRefCode())
                .txnType(tx.getTxnType().name())
                .createdAt(tx.getCreatedAt()
                        .atZoneSameInstant(userZone)
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                )
                .build());
    }

    private Pageable getPageable(String sortDirection, int page, int size) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ?
                Sort.by("createdAt").ascending() :
                Sort.by("createdAt").descending();

        return PageRequest.of(page, size, sort);
    }
}