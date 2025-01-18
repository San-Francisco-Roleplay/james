package com.computiotion.sfrp.bot.infractions;

import com.computiotion.sfrp.bot.reference.ReferencePayload;

public class InfractionReference extends ReferencePayload {
    private final String queueId;

    public InfractionReference(String queueId) {
        this.queueId = queueId;
    }

    public String getQueueId() {
        return queueId;
    }
}
