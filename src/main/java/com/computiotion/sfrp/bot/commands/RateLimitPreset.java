package com.computiotion.sfrp.bot.commands;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.local.LocalBucketBuilder;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public enum RateLimitPreset {
    Infinite(),
    Session(Bucket.builder()
            .addLimit(limit -> limit.capacity(2).refillGreedy(1, Duration.ofSeconds(30))));

    private final @Nullable LocalBucketBuilder bucket;

    RateLimitPreset() {
        this.bucket = null;
    }

    RateLimitPreset(@Nullable LocalBucketBuilder bucket) {
        this.bucket = bucket;
    }

    public @Nullable LocalBucketBuilder getBucket() {
        return bucket;
    }
}
