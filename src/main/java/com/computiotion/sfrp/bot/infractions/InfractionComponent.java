package com.computiotion.sfrp.bot.infractions;

import com.computiotion.sfrp.bot.components.ComponentData;
import com.computiotion.sfrp.bot.Reference;

@Reference("infraction_utils")
public class InfractionComponent extends ComponentData {
    private final String id;

    public InfractionComponent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
