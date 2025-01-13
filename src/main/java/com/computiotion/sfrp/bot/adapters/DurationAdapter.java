package com.computiotion.sfrp.bot.adapters;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(@NotNull JsonWriter writer, @NotNull Duration value) throws IOException {
        writer.value(value.toString());
    }

    @Override
    public Duration read(JsonReader reader) throws IOException {
        JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

        return Duration.parse(jsonObject.getAsString());
    }
}
