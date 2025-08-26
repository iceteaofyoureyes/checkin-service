package com.wiinvent.checkinservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wiinvent.checkinservice.service.CacheService;
import com.wiinvent.checkinservice.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheServiceImpl implements CacheService {

    private final RedissonClient redissonClient;

    @Override
    public <T> T get(String key, Class<T> clazz) throws JsonProcessingException {
        RBucket<String> bucket = redissonClient.getBucket(key);
        String cached = bucket.get();
        if (cached == null) return null;
        return JsonUtils.fromJson(cached, clazz);
    }

    @Override
    public <T> void set(String key, T value, long ttl, TimeUnit unit) throws JsonProcessingException {
        String json = JsonUtils.toJson(value);
        redissonClient.getBucket(key).set(json, ttl, unit);
    }

    @Override
    public void delete(String key) {
        redissonClient.getBucket(key).delete();
    }

    @Override
    public RLock tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        RLock lock = redissonClient.getLock(lockKey);
        lock.tryLock(waitTime, leaseTime, unit);
        return lock;
    }

    @Override
    public void unlock(RLock lock) {
        if (lock != null && lock.isHeldByCurrentThread()) {
            try {
                lock.unlock();
            } catch (Exception ex) {
                log.warn("Failed to unlock", ex);
            }
        }
    }

    @Override
    public <T> T waitForCache(String key, Class<T> clazz, long maxWaitMs, int maxAttempts) throws JsonProcessingException {
        long sleep = 50;
        int attempts = 0;

        while (attempts < maxAttempts) {
            T cached = get(key, clazz);
            if (cached != null) {
                return cached;
            }

            try {
                long jitter = ThreadLocalRandom.current().nextLong(sleep + 1);
                Thread.sleep(jitter);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            attempts++;
            sleep = Math.min(1000, sleep * 2);
        }

        return null;
    }
}
