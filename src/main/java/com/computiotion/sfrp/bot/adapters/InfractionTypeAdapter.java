package com.computiotion.sfrp.bot.adapters;

import com.computiotion.sfrp.bot.infractions.InfractionType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class InfractionTypeAdapter extends TypeAdapter<InfractionType> {
    @Override
    public void write(JsonWriter writer, InfractionType value) throws IOException {
        writer.value(value.ordinal());
    }

    @Override
    public InfractionType read(JsonReader reader) throws IOException {
        JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
        return InfractionType.values()[jsonObject.getAsInt()];
    }
}
