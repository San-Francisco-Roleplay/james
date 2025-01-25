package com.computiotion.sfrp.bot.adapters;

import com.computiotion.sfrp.bot.infractions.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

public class InfractionAdapterFactory implements TypeAdapterFactory {
    private static final Log log = LogFactory.getLog(InfractionAdapterFactory.class);

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!Infraction.class.isAssignableFrom(type.getRawType())) {
            return null; // this class only serializes 'Infraction' and its subtypes
        }

        final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
        final TypeAdapter<InfractionImpl> defaultAdapter = gson.getDelegateAdapter(this, TypeToken.get(InfractionImpl.class));
        final TypeAdapter<QuantitativeInfractionImpl> quantitativeAdapter = gson.getDelegateAdapter(this, TypeToken.get(QuantitativeInfractionImpl.class));
        final TypeAdapter<TimeableInfractionImpl> timeableAdapter = gson.getDelegateAdapter(this, TypeToken.get(TimeableInfractionImpl.class));

        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                JsonObject jsonObject;
                if (value instanceof QuantitativeInfractionImpl) {
                    jsonObject = quantitativeAdapter.toJsonTree((QuantitativeInfractionImpl) value).getAsJsonObject();
                } else if (value instanceof TimeableInfractionImpl) {
                    jsonObject = timeableAdapter.toJsonTree((TimeableInfractionImpl) value).getAsJsonObject();
                } else {
                    jsonObject = defaultAdapter.toJsonTree((InfractionImpl) value).getAsJsonObject();
                }
                jsonObject.addProperty("type", ((Infraction) value).getType().name());
                elementAdapter.write(out, jsonObject);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                JsonElement jsonElement = elementAdapter.read(in);
                if (!jsonElement.isJsonObject()) {
                    throw new JsonSyntaxException("Expected a JSON object but was " + jsonElement);
                }
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String typeStr = jsonObject.get("type").getAsString();
                InfractionType type;
                try {
                    type = InfractionType.valueOf(typeStr);
                } catch (IllegalArgumentException e) {
                    throw new JsonParseException("Invalid InfractionType: " + typeStr);
                }
                return switch (type) {
                    case Warning, Strike -> (T) quantitativeAdapter.fromJsonTree(jsonObject);
                    case Suspend -> {
                        if (jsonObject.has("duration") && jsonObject.get("duration").isJsonPrimitive()) {
                            jsonObject.addProperty("duration", jsonObject.get("duration").getAsString());
                        }
                        yield (T) timeableAdapter.fromJsonTree(jsonObject);
                    }
                    default -> (T) defaultAdapter.fromJsonTree(jsonObject);
                };
            }
        }.nullSafe();
    }
}