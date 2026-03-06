package com.sphere.common.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final ConcurrentHashMap<String, Bucket> postBuckets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Bucket> commentBuckets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Bucket> dmBuckets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Bucket> uploadBuckets = new ConcurrentHashMap<>();

    private Bucket createPostBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofHours(1))))
                .build();
    }

    private Bucket createCommentBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(30, Refill.intervally(30, Duration.ofHours(1))))
                .build();
    }

    private Bucket createDmBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(50, Refill.intervally(50, Duration.ofHours(1))))
                .build();
    }

    private Bucket createUploadBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofDays(1))))
                .build();
    }

    public boolean allowPost(String userEmail) {
        return postBuckets.computeIfAbsent(userEmail, k -> createPostBucket()).tryConsume(1);
    }

    public boolean allowComment(String userEmail) {
        return commentBuckets.computeIfAbsent(userEmail, k -> createCommentBucket()).tryConsume(1);
    }

    public boolean allowDm(String userEmail) {
        return dmBuckets.computeIfAbsent(userEmail, k -> createDmBucket()).tryConsume(1);
    }

    public boolean allowUpload(String userEmail) {
        return uploadBuckets.computeIfAbsent(userEmail, k -> createUploadBucket()).tryConsume(1);
    }
}