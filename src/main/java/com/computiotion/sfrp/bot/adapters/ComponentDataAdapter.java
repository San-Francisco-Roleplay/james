package com.computiotion.sfrp.bot.adapters;

import com.computiotion.sfrp.bot.components.ComponentData;
import com.computiotion.sfrp.bot.components.ComponentManager;
import com.computiotion.sfrp.bot.Reference;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.time.Duration;
import java.time.Instant;

public class ComponentDataAdapter extends TypeAdapter<ComponentData> {
    public static final String TYPE_PROPERTY_NAME = "_ref";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();

    @Override
    public void write(JsonWriter writer, ComponentData value) {
        JsonObject elem = (JsonObject) gson.toJsonTree(value);
        Class<? extends ComponentData> cls = value.getClass();

        Reference ref = cls.getAnnotation(Reference.class);
        elem.addProperty(TYPE_PROPERTY_NAME, ref.value());

        gson.toJson(elem, writer);
    }

    @Override
    public ComponentData read(JsonReader reader) {
        JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
        JsonElement refElem = object.get(TYPE_PROPERTY_NAME);

        String ref = refElem.getAsString();
        object.remove(TYPE_PROPERTY_NAME);

        Class<? extends ComponentData> type = ComponentManager.getType(ref);

        return gson.fromJson(object, type);
    }
}
