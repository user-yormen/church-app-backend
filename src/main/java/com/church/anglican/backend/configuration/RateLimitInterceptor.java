package com.church.anglican.backend.configuration;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    private final long capacity;
    private final long refillPerMinute;
    private final long loginCapacity;
    private final long loginRefillPerMinute;

    public RateLimitInterceptor(
            @Value("${app.rate-limit.capacity:100}") long capacity,
            @Value("${app.rate-limit.refill-per-minute:10}") long refillPerMinute,
            @Value("${app.rate-limit.login.capacity:10}") long loginCapacity,
            @Value("${app.rate-limit.login.refill-per-minute:5}") long loginRefillPerMinute
    ) {
        this.capacity = capacity;
        this.refillPerMinute = refillPerMinute;
        this.loginCapacity = loginCapacity;
        this.loginRefillPerMinute = loginRefillPerMinute;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String ipAddress = getClientIp(request);
        String key = ipAddress + ":" + request.getRequestURI();
        Bucket bucket = cache.computeIfAbsent(key, k -> createBucketForPath(request.getRequestURI()));

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true;
        } else {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "You have exhausted your API Request Quota");
            return false;
        }
    }

    private Bucket createBucketForPath(String path) {
        if (path != null && path.startsWith("/api/v1/auth/")) {
            return Bucket.builder()
                    .addLimit(limit -> limit.capacity(loginCapacity).refillGreedy(loginRefillPerMinute, Duration.ofMinutes(1)))
                    .build();
        }
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(capacity).refillGreedy(refillPerMinute, Duration.ofMinutes(1)))
                .build();
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
