package com.computiotion.sfrp.bot.infractions;

import java.time.Duration;

public class TimeableInfractionImpl extends InfractionImpl implements TimeableInfraction {
    private final Duration duration;

    public TimeableInfractionImpl(InfractionType type, Duration duration) {
        super(type);
        this.duration = duration;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }
}
