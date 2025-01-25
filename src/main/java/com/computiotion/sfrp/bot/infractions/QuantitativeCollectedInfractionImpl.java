package com.computiotion.sfrp.bot.infractions;

public class QuantitativeCollectedInfractionImpl extends CollectedInfractionImpl implements CollectedInfraction, QuantitativeInfraction {
    private int count;

    public int getCount() {
        return count;
    }
}
