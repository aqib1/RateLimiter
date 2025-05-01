package org.rate.limiter.wise.domain.limiter.record;

public record RateLimiterConfig(
        int allowedRequestThreshold,
        long timeWindowThreshold,
        int tokenThreshold,
        int refillRatePerSecond
) {
}
