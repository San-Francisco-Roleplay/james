package com.computiotion.sfrp.bot.infractions;

import com.computiotion.sfrp.bot.Emoji;

public enum InfractionType {
    Warning(Emoji.InfractWarning, "Warning", CountableInfraction.class),
    Strike(Emoji.InfractStrike, "Strike", CountableInfraction.class),
    Suspend(Emoji.InfractSuspend, "Suspension", TimeableInfraction.class),
    Trial(Emoji.InfractTrial, "Trial", Infraction.class),
    AdminLeave(Emoji.InfractLeave, "Administrative Leave", Infraction.class),
    Demotion(Emoji.InfractDemotion, "Demotion", Infraction.class),
    Blacklist(Emoji.InfractTermination, "Blacklist", Infraction.class),
    Termination(Emoji.InfractTermination, "Termination", Infraction.class),
    ;

    private final Emoji emoji;
    private final String display;
    private final Class<? extends Infraction> type;

    InfractionType(Emoji emoji, String display, Class<? extends Infraction> type) {
        this.emoji = emoji;
        this.display = display;
        this.type = type;
    }

    public Emoji getEmoji() {
        return emoji;
    }

    public String getDisplay() {
        return display;
    }

    public Class<? extends Infraction> getType() {
        return type;
    }
}
