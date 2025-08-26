package com.wiinvent.checkinservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wiinvent.checkinservice.dto.CheckinConfigDto;
import com.wiinvent.checkinservice.entity.CheckinConfig;
import com.wiinvent.checkinservice.exception.AppException;
import com.wiinvent.checkinservice.exception.ErrorCode;
import com.wiinvent.checkinservice.mapper.CheckinConfigMapper;
import com.wiinvent.checkinservice.repository.CheckinConfigRepository;
import com.wiinvent.checkinservice.service.CacheService;
import com.wiinvent.checkinservice.service.CheckinConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckinConfigServiceImpl implements CheckinConfigService {

    private static final String CACHE_KEY = "checkin:config:active";
    private static final String LOCK_KEY = "checkin:config:lock";
    private static final long BASE_TTL_SECONDS = 3600; // 1 hour

    private final CheckinConfigRepository checkinConfigRepository;
    private final CheckinConfigMapper checkinConfigMapper;
    private final CacheService cacheService;

    @Override
    public CheckinConfigDto getActiveConfig() {
        try {
            // 1. Try get cache
            CheckinConfigDto cached = cacheService.get(CACHE_KEY, CheckinConfigDto.class);

            if (cached != null) {
                return cached;
            }

            // 2. Try lock
            RLock lock = cacheService.tryLock(LOCK_KEY, 200, 10_000, TimeUnit.MILLISECONDS);
            if (lock.isLocked()) {
                try {
                    // Double check after lock
                    cached = cacheService.get(CACHE_KEY, CheckinConfigDto.class);

                    if (cached != null) {
                        return cached;
                    }

                    // Load from DB
                    CheckinConfig entity = checkinConfigRepository.findFirstByIsActiveTrue()
                            .orElseThrow(() -> new AppException(ErrorCode.UNKNOWN_EXCEPTION, "Active checkin config not found"));

                    CheckinConfigDto configDto = checkinConfigMapper.toCheckinConfigDto(entity);
                    cacheService.set(CACHE_KEY, configDto, BASE_TTL_SECONDS, TimeUnit.SECONDS);

                    return configDto;
                } finally {
                    cacheService.unlock(lock);
                }
            } else {
                // 3. Wait for cache then fallback to DB
                cached = cacheService.waitForCache(CACHE_KEY, CheckinConfigDto.class, 3000, 6);

                if (cached != null) {
                    return cached;
                }

                CheckinConfig entity = checkinConfigRepository.findFirstByIsActiveTrue()
                        .orElseThrow(() -> new AppException(ErrorCode.UNKNOWN_EXCEPTION, "Active checkin config not found"));
                return checkinConfigMapper.toCheckinConfigDto(entity);
            }
        } catch (JsonProcessingException | InterruptedException e) {
            log.error(e.getMessage());
            throw new AppException(ErrorCode.UNKNOWN_EXCEPTION, "Error handling cache for checkin config");
        }
    }
}
