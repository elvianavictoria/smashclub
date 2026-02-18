package com.backendsyndicate.smashclub.admin.security.request;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class AdminRateLimitUtility {
    protected Bucket generateBucket(long capacity, Duration interval) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillGreedy(capacity, interval)
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
