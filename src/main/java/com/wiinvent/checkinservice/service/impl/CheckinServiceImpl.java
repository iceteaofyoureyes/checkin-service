package com.wiinvent.checkinservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wiinvent.checkinservice.dto.CheckinConfigDto;
import com.wiinvent.checkinservice.dto.CheckinDayDTO;
import com.wiinvent.checkinservice.dto.response.CheckinResponse;
import com.wiinvent.checkinservice.dto.response.MonthCheckinResponse;
import com.wiinvent.checkinservice.entity.CheckinLog;
import com.wiinvent.checkinservice.entity.User;
import com.wiinvent.checkinservice.entity.Wallet;
import com.wiinvent.checkinservice.entity.WalletTransaction;
import com.wiinvent.checkinservice.entity.enums.TransactionType;
import com.wiinvent.checkinservice.exception.AppException;
import com.wiinvent.checkinservice.exception.ErrorCode;
import com.wiinvent.checkinservice.exception.ResourceNotFoundException;
import com.wiinvent.checkinservice.repository.CheckinLogRepository;
import com.wiinvent.checkinservice.repository.UserRepository;
import com.wiinvent.checkinservice.repository.WalletRepository;
import com.wiinvent.checkinservice.repository.WalletTransactionRepository;
import com.wiinvent.checkinservice.service.CacheService;
import com.wiinvent.checkinservice.service.CheckinConfigService;
import com.wiinvent.checkinservice.service.CheckinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckinServiceImpl implements CheckinService {

    private static final DateTimeFormatter KEY_DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final CheckinLogRepository checkinLogRepo;
    private final WalletRepository walletRepo;
    private final WalletTransactionRepository txnRepo;
    private final UserRepository userRepo;
    private final CacheService cacheService;
    private final CheckinConfigService configService;

    @Override
    @Transactional
    public CheckinResponse checkin(String username, ZoneId userZone) {
        // todo update code handle case gui request nhung den server bi tre qua gio diem danh (client phai gui len thoi gian diem danh)

        // check time windows
        if (!isInValidTimeWindow(userZone)) {
            throw new AppException(ErrorCode.BUSINESS_RULE_EXCEPTION, "Check-in failed. Not in valid time");
        }

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Long userId = user.getUserId();
        LocalDate utcDate = computeUtcStoredDate(userZone);
        String lockKey = buildLockKey(utcDate, userId);
        String cacheKey = buildCacheKey(utcDate, userId);

        RLock lock = null;
        try {

            // Check cache (idempotency shortcut)
            if (cacheService.get(cacheKey, Boolean.class) != null && cacheService.get(cacheKey, Boolean.class)) {
                return buildAlreadyCheckedResponse();
            }

            lock = cacheService.tryLock(lockKey, 3000, 10_000, TimeUnit.MILLISECONDS);
            if (lock == null || !lock.isLocked()) {
                throw new AppException(ErrorCode.UNKNOWN_EXCEPTION, "System busy, please retry later");
            }

            // Double-check cache after get lock
            if (cacheService.get(cacheKey, Boolean.class) != null && cacheService.get(cacheKey, Boolean.class)) {
                return buildAlreadyCheckedResponse();
            }

            // Check DB in case cache miss
            if (checkAlreadyCheckedIn(userId, utcDate, cacheKey)) {
                return buildAlreadyCheckedResponse();
            }

            //  Check month limit checkin
            int timesThisMonth = countTimesInMonth(userId, utcDate);
            int monthlyLimit = configService.getActiveConfig().getMonthlyLimitDays();
            if (timesThisMonth >= monthlyLimit) {
                return buildLimitReachedResponse(timesThisMonth);
            }

            Wallet wallet = walletRepo.findByUser_UserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
            int pointToAdd = configService.resolvePointForNth(timesThisMonth + 1);

            // Persist checkin log + txn
            persistCheckin(user, utcDate, pointToAdd, wallet);

            // process claim reward
            processReward(wallet, pointToAdd);

            // cache flag (till day end)
            long ttl = computeTtlUntilEndOfUtcDate(utcDate);
            cacheService.set(cacheKey, true, ttl, TimeUnit.MILLISECONDS);

            return buildSuccessResponse(timesThisMonth + 1, pointToAdd);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            log.error("Interrupted while acquiring lock", e);
            throw new AppException(ErrorCode.UNKNOWN_EXCEPTION, "System error occurs");

        } catch (JsonProcessingException e) {

            log.error("Json processing error", e);
            throw new AppException(ErrorCode.UNKNOWN_EXCEPTION, "System error occurs");

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

    @Override
    public MonthCheckinResponse getMonthCheckins(String username, ZoneId userZone) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Long userId = user.getUserId();
        LocalDate nowUtc = LocalDate.now(ZoneOffset.UTC);
        LocalDate startOfMonth = nowUtc.withDayOfMonth(1);
        LocalDate endOfMonth = nowUtc.withDayOfMonth(nowUtc.lengthOfMonth());

        List<CheckinLog> checkinLogs = checkinLogRepo
                .findByUserAndDateRange(userId, startOfMonth, endOfMonth);

        checkinLogs.sort(Comparator.comparing(CheckinLog::getCheckinTime));
        CheckinConfigDto config = configService.getActiveConfig();
        List<Integer> pointConfigs = config.getPayload().getPointConfigs();
        List<CheckinDayDTO> monthCheckins = new ArrayList<>();

        for (int i = 0; i < pointConfigs.size(); i++) {
            int nth = i + 1;
            CheckinDayDTO dto = CheckinDayDTO.builder()
                    .name("Day " + nth)
                    .build();
            dto.setPointAward(pointConfigs.get(i));

            if (i < checkinLogs.size()) {
                CheckinLog  checkinLog = checkinLogs.get(i);
                OffsetDateTime utcTime = checkinLog.getCheckinTime();
                ZonedDateTime userTime = utcTime.atZoneSameInstant(userZone);

                dto.setChecked(true);
                dto.setCheckinDate(userTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            } else {
                dto.setChecked(false);
                dto.setCheckinDate(null);
            }

            monthCheckins.add(dto);
        }

        return new MonthCheckinResponse(monthCheckins);
    }

    private boolean isInValidTimeWindow(ZoneId userZone) {
        ZonedDateTime userNow = ZonedDateTime.now(userZone);
        ZonedDateTime utcNow = userNow.withZoneSameInstant(ZoneOffset.UTC);
        LocalTime utcTime = utcNow.toLocalTime();
        return configService.isWithinTimeWindow(utcTime);
    }

    private LocalDate computeUtcStoredDate(ZoneId userZone) {
        Instant now = Instant.now();
        ZonedDateTime utcTimeFromClientZone = now.atZone(userZone).withZoneSameInstant(ZoneOffset.UTC);
        return utcTimeFromClientZone.toLocalDate();
    }

    private String buildLockKey(LocalDate utcDate, Long userId) {
        return "checkin:" + KEY_DATE_FMT.format(utcDate) + ":user:" + userId;
    }

    private boolean checkAlreadyCheckedIn(Long userId, LocalDate utcDate, String cacheKey) throws JsonProcessingException {
        boolean exists = checkinLogRepo.existsByUser_UserIdAndCheckinDate(userId, utcDate);
        if (exists) {
            // sync cache
            long ttlMillis = computeTtlUntilEndOfUtcDate(utcDate);
            cacheService.set(cacheKey, true, ttlMillis, TimeUnit.MILLISECONDS);
        }
        return exists;
    }

    private int countTimesInMonth(Long userId, LocalDate utcDate) {
        LocalDate startDateOfMonth = utcDate.withDayOfMonth(1);
        LocalDate endDateOfMonth = utcDate.withDayOfMonth(utcDate.lengthOfMonth());
        return checkinLogRepo.countByUserIdAndCheckinDateBetween(userId, startDateOfMonth, endDateOfMonth);
    }

    private void processReward(Wallet wallet, long pointToAdd) {
        wallet.setBalance(wallet.getBalance() + pointToAdd);
        wallet.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        walletRepo.save(wallet);
    }

    private void persistCheckin(User user, LocalDate utcDate, long pointToAdd, Wallet wallet) {
        CheckinLog checkinLog = checkinLogRepo.save(CheckinLog.builder()
                .user(user)
                .checkinDate(utcDate)
                .checkinTime(OffsetDateTime.now(ZoneOffset.UTC))
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        String txnRef = generateTxnRef(user.getUserId(), utcDate);
        try {
            txnRepo.save(WalletTransaction.builder()
                    .wallet(wallet)
                    .amount(pointToAdd)
                    .txnType(TransactionType.REWARD)
                    .refId(checkinLog.getId())
                    .refCode(txnRef)
                    .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                    .build());
        } catch (DataIntegrityViolationException e) {
            log.warn("TxRef duplicate (idempotent case) for refCode={}, userId={}", txnRef, user.getUserId());
            throw new AppException(ErrorCode.UNKNOWN_EXCEPTION, "Error has occurs");
        }
    }

    private CheckinResponse buildLimitReachedResponse(int timesThisMonth) {
        return CheckinResponse.builder()
                .success(false)
                .message("Monthly check-in limit reached")
                .timesThisMonth(timesThisMonth)
                .build();
    }

    private CheckinResponse buildSuccessResponse(int timesThisMonth, int pointToAdd) {
        return CheckinResponse.builder()
                .success(true)
                .message("Check-in successful")
                .earnedPoint(pointToAdd)
                .timesThisMonth(timesThisMonth)
                .build();
    }

    private CheckinResponse buildAlreadyCheckedResponse() {
        return CheckinResponse.builder()
                .success(false)
                .message("Already checked in today")
                .build();
    }


    private String generateTxnRef(Long userId, LocalDate utcDate) {
        return String.format("CHECKIN-%d-%s", userId, KEY_DATE_FMT.format(utcDate));
    }

    private String buildCacheKey(LocalDate utcDate, Long userId) {
        return "checkin:flag:" + userId + ":" + utcDate;
    }

    private long computeTtlUntilEndOfUtcDate(LocalDate utcDate) {
        ZonedDateTime endOfDayUtc = utcDate.plusDays(1)
                .atStartOfDay(ZoneOffset.UTC);
        Instant nowUtc = Instant.now();
        return Duration.between(nowUtc, endOfDayUtc.toInstant()).toMillis();
    }


}
