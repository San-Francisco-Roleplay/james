package com.computiotion.sfrp.bot.adapters;

import com.computiotion.sfrp.bot.Reference;
import com.computiotion.sfrp.bot.reference.ReferenceManager;
import com.computiotion.sfrp.bot.reference.ReferencePayload;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.time.Duration;
import java.time.Instant;

public class ReferencePayloadAdapter extends TypeAdapter<ReferencePayload> {
    public static final String TYPE_PROPERTY_NAME = "_ref";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();
    private static final Log log = LogFactory.getLog(ReferencePayloadAdapter.class);

    @Override
    public void write(JsonWriter writer, ReferencePayload value) {
        JsonObject elem = (JsonObject) gson.toJsonTree(value);
        Class<? extends ReferencePayload> cls = value.getClass();

        Reference ref = cls.getAnnotation(Reference.class);
        elem.addProperty(TYPE_PROPERTY_NAME, ref.value());

        gson.toJson(elem, writer);
    }

    @Override
    public ReferencePayload read(JsonReader reader) {
        JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
        log.debug("Reading JSON object: " + object.toString());
        JsonElement refElem = object.get(TYPE_PROPERTY_NAME);

        if (refElem == null) {
            log.error("Reference element is missing in JSON object.");
            return null;
        }

        String ref = refElem.getAsString();
        object.remove(TYPE_PROPERTY_NAME);

        Class<? extends ReferencePayload> type = ReferenceManager.getType(ref);
        if (type == null) {
            log.error("No type found for reference: " + ref);
            return null;
        }

        log.debug("Determined type: " + type.getName());
        return gson.fromJson(object, type);
    }
}
