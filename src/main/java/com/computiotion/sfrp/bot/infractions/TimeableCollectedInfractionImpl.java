package com.computiotion.sfrp.bot.infractions;

import java.time.Duration;

public class TimeableCollectedInfractionImpl extends CollectedInfractionImpl implements TimeableCollectedInfraction {
    private final long duration;

    public TimeableCollectedInfractionImpl(Duration duration) {
        this.duration = duration.toMillis();
    }

    @Override
    public Duration getDuration() {
        return Duration.ofMillis(duration);
    }
}
