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
    private final ConcurrentHashMap<String, Bucket> photoBuckets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Bucket> videoBuckets = new ConcurrentHashMap<>();

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

    // 3 photos per day
    private Bucket createPhotoBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(3, Refill.intervally(3, Duration.ofDays(1))))
                .build();
    }

    // 1 video per day
    private Bucket createVideoBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(1, Refill.intervally(1, Duration.ofDays(1))))
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

    // count per photo — call once per photo uploaded (max 3)
    public boolean allowPhotoUpload(String userEmail, int count) {
        return photoBuckets.computeIfAbsent(userEmail, k -> createPhotoBucket()).tryConsume(count);
    }

    // 1 video per day
    public boolean allowVideoUpload(String userEmail) {
        return videoBuckets.computeIfAbsent(userEmail, k -> createVideoBucket()).tryConsume(1);
    }
}