package com.computiotion.sfrp.bot.infractions;

import java.time.Duration;

public interface TimeableInfraction extends Infraction {
    Duration getDuration();
}
