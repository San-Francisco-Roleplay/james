package com.computiotion.sfrp.bot.infractions;

import java.time.Duration;

public class TimeableInfraction extends Infraction {
    private Duration duration;

    public Duration getDuration() {
        return duration;
    }
}
