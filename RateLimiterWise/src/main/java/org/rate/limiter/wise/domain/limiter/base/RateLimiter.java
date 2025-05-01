package org.rate.limiter.wise.domain.limiter.base;

public interface RateLimiter {
    boolean tryAcquire();
    void reset();
}
