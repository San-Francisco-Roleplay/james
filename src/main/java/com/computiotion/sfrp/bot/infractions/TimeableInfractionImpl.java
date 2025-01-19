package com.computiotion.sfrp.bot.infractions;

import java.time.Duration;

public class TimeableInfractionImpl extends InfractionImpl implements TimeableInfraction {
    private final long duration;

    public TimeableInfractionImpl(InfractionType type, Duration duration) {
        super(type);
        this.duration = duration.toMillis();
    }

    @Override
    public Duration getDuration() {
        return Duration.ofMillis(duration);
    }
}
