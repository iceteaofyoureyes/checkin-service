package com.wiinvent.checkinservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

public interface CacheService {

    <T> T get(String key, Class<T> clazz) throws JsonProcessingException;

    <T> void set(String key, T value, long ttl, TimeUnit unit) throws JsonProcessingException;

    void delete(String key);

    RLock tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException;

    void unlock(RLock lock);

    <T> T waitForCache(String key, Class<T> clazz, long maxWaitMs, int maxAttempts) throws JsonProcessingException;
}
