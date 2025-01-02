package com.computiotion.sfrp.bot;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.time.Instant;

public class Snowflake {
    /**
     * The timestamp after the Unix epoch that should be the basis for all dates. Defaults to 0.
     * Forty-two bits.
     */
    private Timestamp epoch;
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

    public Snowflake(@Nullable Timestamp epoch, @Nullable Integer increment, @Nullable Integer pid, @Nullable Integer worker) {
        this.epoch = epoch;
        this.increment = increment;
        this.pid = pid;
        this.worker = worker;
    }

    public Snowflake() {
        this.epoch = new Timestamp(0);
        this.increment = 0;
        this.pid = 0;
        this.worker = 0;
    }

    /**
     * @return the specified epoch
     */
    public Timestamp getEpoch() {
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

    public Snowflake setEpoch(Timestamp epoch) {
        this.epoch = epoch;
        return this;
    }

    public String generate() {
        return String.valueOf(generateAsLong(Timestamp.from(Instant.now())));
    }

    public String generate(Timestamp timestamp) {
        return String.valueOf(generateAsLong(timestamp));
    }

    public Long generateAsLong() {
        return generateAsLong(Timestamp.from(Instant.now()));
    }

    public Long generateAsLong(Timestamp timestamp) {
        if (epoch == null) epoch = new Timestamp(0);
        if (increment == null) increment = 0;
        if (pid == null) pid = 0;
        if (worker == null) worker = 0;

        long refTimestamp = timestamp.getTime() - epoch.getTime();

        String binaryTime = StringUtils.leftPad(Long.toBinaryString(refTimestamp), 42, "0");
        String binaryWorker = StringUtils.leftPad(Integer.toBinaryString(worker), 5, "0");
        String binaryPid = StringUtils.leftPad(Integer.toBinaryString(pid), 5, "0");
        String binaryIncrement = StringUtils.leftPad(Integer.toBinaryString(increment % 4095), 12, "0");

        return Long.parseLong(binaryTime + binaryWorker + binaryPid + binaryIncrement, 2);
    }
}