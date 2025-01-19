package com.computiotion.sfrp.bot.infractions;

public class QuantitativeInfractionImpl implements QuantitativeInfraction {
    private InfractionType type;
    private int count;

    public QuantitativeInfractionImpl(InfractionType type, int count) {
        this.type = type;
        this.count = count;
    }

    public int getCount() {
        return count;
    }
    public InfractionType getType() {
        return type;
    }
}
