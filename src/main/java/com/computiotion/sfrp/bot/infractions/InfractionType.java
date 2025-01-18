package com.computiotion.sfrp.bot.infractions;

import com.computiotion.sfrp.bot.Emoji;

public enum InfractionType {
    Warning(Emoji.InfractWarning, "Warning", QuantitativeCollectedInfractionImpl.class, QuantitativeInfractionImpl.class),
    Strike(Emoji.InfractStrike, "Strike", QuantitativeCollectedInfractionImpl.class, QuantitativeInfractionImpl.class),
    Suspend(Emoji.InfractSuspend, "Suspension", TimeableCollectedInfractionImpl.class, TimeableInfractionImpl.class),
    Trial(Emoji.InfractTrial, "Trial", CollectedInfractionImpl.class, InfractionImpl.class),
    AdminLeave(Emoji.InfractLeave, "Administrative Leave", CollectedInfractionImpl.class, InfractionImpl.class),
    Demotion(Emoji.InfractDemotion, "Demotion", CollectedInfractionImpl.class, InfractionImpl.class),
    Blacklist(Emoji.InfractTermination, "Blacklist", CollectedInfractionImpl.class, InfractionImpl.class),
    Termination(Emoji.InfractTermination, "Termination", CollectedInfractionImpl.class, InfractionImpl.class),
    ;

    private final Emoji emoji;
    private final String display;
    private final Class<? extends CollectedInfraction> collectedType;
    private final Class<? extends Infraction> type;

    InfractionType(Emoji emoji, String display, Class<? extends CollectedInfraction> collectedType, Class<? extends Infraction> type) {
        this.emoji = emoji;
        this.display = display;
        this.collectedType = collectedType;
        this.type = type;
    }

    public Emoji getEmoji() {
        return emoji;
    }

    public String getDisplay() {
        return display;
    }

    public Class<? extends CollectedInfraction> getCollectedType() {
        return collectedType;
    }

    public Class<? extends Infraction> getType() {
        return type;
    }

}
