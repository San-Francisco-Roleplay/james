package com.computiotion.sfrp.bot;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

public class Snowflake {
    private final Instant DEFAULT_EPOCH = Instant.ofEpochSecond(1735603200);
    /**
     * The timestamp after the Unix epoch that should be the basis for all dates. Defaults to 0.
     * Forty-two bits.
     */
    private Instant epoch;
    /**
     * Internal process ID, in decimal.
     * 5 bits, max = 31.
     */
    private Integer pid;
    /**
     * Internal worker ID, in decimal.
     * 5 bits, max = 31.
     */
    private Integer worker;
    /**
     * Incremented for every generated ID on that process, in decimal.
     * 12 bits, max = 4095.
     */
    private Integer increment;

    public Snowflake(@Nullable Instant epoch, @Nullable Integer increment, @Nullable Integer pid, @Nullable Integer worker) {
        this.epoch = epoch == null ? DEFAULT_EPOCH : epoch;
        this.increment = increment == null ? 0 : increment;
        this.pid = pid == null ? 0 : pid;
        this.worker = worker == null ? 0 : worker;
    }

    public Snowflake() {
        this.epoch = DEFAULT_EPOCH;
        this.increment = 0;
        this.pid = 0;
        this.worker = 0;
    }

    /**
     * @return the specified epoch
     */
    public Instant getEpoch() {
        return epoch;
    }

    public int getIncrement() {
        return increment;
    }

    public Snowflake setIncrement(Integer increment) {
        this.increment = increment;
        return this;
    }

    public Snowflake increaseIncrement() { this.increment++; return this; }

    public String nextId() { this.increment++; return generate(); }


    public int getWorker() {
        return worker;
    }

    public Snowflake setWorker(Integer worker) {
        this.worker = worker;
        return this;
    }

    public int getPid() {
        return pid;
    }

    public Snowflake setPid(Integer pid) {
        this.pid = pid;
        return this;
    }

    public Snowflake setEpoch(Instant epoch) {
        this.epoch = epoch;
        return this;
    }

    public String generate() {
        return String.valueOf(generateAsLong(Instant.now()));
    }

    public String generate(Instant instant) {
        return String.valueOf(generateAsLong(instant));
    }

    public Long generateAsLong() {
        return generateAsLong(Instant.now());
    }

    public Long generateAsLong(Instant instant) {
        if (epoch == null) epoch = DEFAULT_EPOCH;
        if (increment == null) increment = 0;
        if (pid == null) pid = 0;
        if (worker == null) worker = 0;

        long refTimestamp = instant.toEpochMilli() - epoch.toEpochMilli();

        String binaryTime = StringUtils.leftPad(Long.toBinaryString(refTimestamp), 42, "0");
        String binaryWorker = StringUtils.leftPad(Integer.toBinaryString(worker), 5, "0");
        String binaryPid = StringUtils.leftPad(Integer.toBinaryString(pid), 5, "0");
        String binaryIncrement = StringUtils.leftPad(Integer.toBinaryString(increment % 4095), 12, "0");

        return Long.parseLong(binaryTime + binaryWorker + binaryPid + binaryIncrement, 2);
    }
}