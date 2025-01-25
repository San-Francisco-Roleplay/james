package com.computiotion.sfrp.bot.infractions;

import com.computiotion.sfrp.bot.Reference;
import com.computiotion.sfrp.bot.reference.ReferencePayload;
import com.google.gson.annotations.SerializedName;

@Reference("infraction_ref")
public class InfractionReference extends ReferencePayload {
    @SerializedName("queue_id")
    private final String queueId;

    public InfractionReference(String queueId) {
        this.queueId = queueId;
    }

    public String getQueueId() {
        return queueId;
    }
}
