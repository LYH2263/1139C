package com.wordmind.security;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimiter {

    // IP -> 尝试次数
    private final ConcurrentHashMap<String, AtomicInteger> attempts = new ConcurrentHashMap<>();
    // IP -> 锁定时间
    private final ConcurrentHashMap<String, Long> lockoutTime = new ConcurrentHashMap<>();

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION = 5 * 60 * 1000; // 5分钟

    public boolean isBlocked(String ip) {
        Long lockTime = lockoutTime.get(ip);
        if (lockTime != null) {
            if (System.currentTimeMillis() - lockTime < LOCKOUT_DURATION) {
                return true;
            } else {
                // 解锁
                lockoutTime.remove(ip);
                attempts.remove(ip);
            }
        }
        return false;
    }

    public void recordFailedAttempt(String ip) {
        AtomicInteger count = attempts.computeIfAbsent(ip, k -> new AtomicInteger(0));
        if (count.incrementAndGet() >= MAX_ATTEMPTS) {
            lockoutTime.put(ip, System.currentTimeMillis());
        }
    }

    public void resetAttempts(String ip) {
        attempts.remove(ip);
        lockoutTime.remove(ip);
    }

    public int getRemainingAttempts(String ip) {
        AtomicInteger count = attempts.get(ip);
        return count == null ? MAX_ATTEMPTS : Math.max(0, MAX_ATTEMPTS - count.get());
    }
}
