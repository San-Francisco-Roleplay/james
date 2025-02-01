package com.computiotion.sfrp.bot.infractions;

public class CollectedInfractionImpl implements Infraction, CollectedInfraction {
    private transient String collectionId;
    private InfractionType type;

    public String getCollectionId() {
        return collectionId;
    }

    public InfractionCollection getCollection() {
        return InfractionCollection.getCollection(collectionId);
    }

    public InfractionType getType() {
        return type;
    }
}
