package com.computiotion.sfrp.bot.infractions;

public class InfractionImpl implements Infraction {
    private final InfractionType type;

    public InfractionImpl(InfractionType type) {
        this.type = type;
    }

    @Override
    public InfractionType getType() {
        return type;
    }

    public InfractionType type() {
        return type;
    }
}
