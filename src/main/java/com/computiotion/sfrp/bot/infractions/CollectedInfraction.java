package com.computiotion.sfrp.bot.infractions;

public interface CollectedInfraction extends Infraction {
    String getCollectionId();
    InfractionCollection getCollection();
}
