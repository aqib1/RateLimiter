package org.rate.limiter.wise.domain.limiter.impl;

import org.rate.limiter.wise.domain.limiter.base.RateLimiter;
import org.rate.limiter.wise.domain.limiter.record.RateLimiterConfig;

import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FixedWindowRateLimiter implements RateLimiter {
    private final Lock lock;
    private final RateLimiterConfig config;
    private int currentRequestCount;
    private long currentStartWindow;
    public FixedWindowRateLimiter(RateLimiterConfig config) {
        this.config = config;
        this.lock = new ReentrantLock();
        this.currentStartWindow = Instant.now().toEpochMilli();
    }

    @Override
    public boolean tryAcquire() {
        lock.lock();
        try {
            long now = Instant.now().toEpochMilli();
            if((now - currentStartWindow) >= config.timeWindowThreshold()) {
                currentStartWindow = now;
                currentRequestCount = 0;
            }

            if(currentRequestCount <= config.allowedRequestThreshold()) {
                currentRequestCount++;
                return true;
            }

            return false;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void reset() {
        currentRequestCount = 0;
        currentStartWindow = Instant.now().toEpochMilli();
    }
}
