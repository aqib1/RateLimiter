package org.rate.limiter.wise.domain.limiter.impl;

import org.rate.limiter.wise.domain.limiter.base.RateLimiter;
import org.rate.limiter.wise.domain.limiter.record.RateLimiterConfig;

import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SlidingWindowRateLimiter implements RateLimiter {
    private final RateLimiterConfig config;
    private final Queue<Long> slidingWindow;
    private final Lock lock;

    public SlidingWindowRateLimiter(RateLimiterConfig config) {
        this.config = config;
        this.slidingWindow = new LinkedList<>();
        this.lock = new ReentrantLock();
    }

    @Override
    public boolean tryAcquire() {
        lock.lock();
        try {
            long now = Instant.now().toEpochMilli();

            while (!slidingWindow.isEmpty()
                    && (now - slidingWindow.peek()) >= config.timeWindowThreshold()) {
                slidingWindow.poll();
            }

            if (slidingWindow.size() <= config.allowedRequestThreshold()) {
                slidingWindow.add(now);
                return true;
            }

            return false;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void reset() {
        slidingWindow.clear();
    }
}
