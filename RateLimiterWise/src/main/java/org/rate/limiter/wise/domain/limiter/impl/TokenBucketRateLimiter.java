package org.rate.limiter.wise.domain.limiter.impl;

import org.rate.limiter.wise.domain.limiter.base.RateLimiter;
import org.rate.limiter.wise.domain.limiter.record.RateLimiterConfig;

import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

public class TokenBucketRateLimiter implements RateLimiter {
    private final RateLimiterConfig config;
    private final ReentrantLock lock;
    private long lastRefillTimestampMillis;
    private int tokens;
    public TokenBucketRateLimiter(RateLimiterConfig config) {
        this.config = config;
        this.lock = new ReentrantLock();
        this.lastRefillTimestampMillis = Instant.now().toEpochMilli();
    }

    @Override
    public boolean tryAcquire() {
        lock.lock();
        try {
            refill();
            if(tokens >= 1) {
                tokens--;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    private void refill() {
        long now = Instant.now().toEpochMilli();
        int refill = (int)((now - lastRefillTimestampMillis) / 1000) * config.refillRatePerSecond();
        tokens = Math.min(config.tokenThreshold(), tokens + refill);
        lastRefillTimestampMillis = now;
    }

    @Override
    public void reset() {
        lock.lock();
        try {
            tokens = config.tokenThreshold();
            lastRefillTimestampMillis = Instant.now().toEpochMilli();
        } finally {
            lock.unlock();
        }
    }
}
