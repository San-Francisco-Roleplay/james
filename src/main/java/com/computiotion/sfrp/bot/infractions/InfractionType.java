package com.computiotion.sfrp.bot.infractions;

import com.computiotion.sfrp.bot.Emoji;

public enum InfractionType {
    Warning("warn", Emoji.InfractWarning, "Warning", QuantitativeCollectedInfractionImpl.class, QuantitativeInfractionImpl.class),
    Strike("strike", Emoji.InfractStrike, "Strike", QuantitativeCollectedInfractionImpl.class, QuantitativeInfractionImpl.class),
    Suspend("suspend", Emoji.InfractSuspend, "Suspension", TimeableCollectedInfractionImpl.class, TimeableInfractionImpl.class),
    Trial("trial", Emoji.InfractTrial, "Trial", CollectedInfractionImpl.class, InfractionImpl.class),
    AdminLeave("leave", Emoji.InfractLeave, "Administrative Leave", CollectedInfractionImpl.class, InfractionImpl.class),
    Demotion("demotion", Emoji.InfractDemotion, "Demotion", CollectedInfractionImpl.class, InfractionImpl.class),
    Blacklist("blacklist", Emoji.InfractTermination, "Blacklist", CollectedInfractionImpl.class, InfractionImpl.class),
    Termination("term", Emoji.InfractTermination, "Termination", CollectedInfractionImpl.class, InfractionImpl.class),
    ;

    private final String command;
    private final Emoji emoji;
    private final String display;
    private final Class<? extends CollectedInfraction> collectedType;
    private final Class<? extends Infraction> type;

    InfractionType(String command, Emoji emoji, String display, Class<? extends CollectedInfraction> collectedType, Class<? extends Infraction> type) {
        this.command = command;
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

    public String getCommand() {
        return command;
    }
}
