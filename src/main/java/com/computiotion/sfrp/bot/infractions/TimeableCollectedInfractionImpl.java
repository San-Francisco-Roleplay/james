package com.computiotion.sfrp.bot.infractions;

import java.time.Duration;

public class TimeableCollectedInfractionImpl extends CollectedInfractionImpl implements TimeableCollectedInfraction {
    private final Duration duration;

    public TimeableCollectedInfractionImpl(Duration duration) {
        this.duration = duration;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }
}
