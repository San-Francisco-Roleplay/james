package com.computiotion.sfrp.bot.erm;

import com.computiotion.sfrp.bot.ConfigManager;
import com.computiotion.sfrp.bot.Generators;
import com.computiotion.sfrp.bot.Hooks;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

public class ERMUtils {
    public static final String BASE_URL = "https://core.ermbot.xyz/api/v1";
    private static final String guildId = ConfigManager.getErmGuild();
    private static final String apiKey = ConfigManager.getErmAPIKey();
    private static final Gson gson = Generators.getGson();
    private static final Log log = LogFactory.getLog(ERMUtils.class);

    public static @NotNull @Unmodifiable List<@NotNull Shift> getShifts() {
        HttpResponse<String> response = Unirest.get(BASE_URL + "/shifts")
                .header("Authorization", apiKey)
                .header("Guild", guildId)
                .asString();
        log.trace("ERM Response Received");

        if (response.getStatus() == 404) {
            log.debug("None found.");
            return List.of();
        } else if (response.getStatus() != 200) {
            log.error("Status Code: " + response.getStatus() + ", body: " + response.getBody());
            return List.of();
        }

        log.trace("Getting body.");
        String rawBody = response.getBody();
        log.trace("Body received.");
        List<Shift> shifts = new ArrayList<>();

        log.trace("Parsing body.");
        JsonObject object = Hooks.useBody(rawBody);
        assert object != null;
        JsonArray array = object.get("data").getAsJsonArray();
        for (JsonElement elem : array) {
            log.trace("Checking element.");
            if (!elem.isJsonObject()) continue;
            JsonObject element = (JsonObject) elem;
            log.trace("Found element.");

            log.trace("Starting deserialization of " + elem.toString());
            Shift shift = gson.fromJson(elem, Shift.class);
            log.trace("Deserialization completed.");
            log.trace("Set id.");
            shifts.add(shift);
            log.trace("Added element.");
        }

        log.trace("Returning.");
        return shifts;
    }

    public static @NotNull @Unmodifiable List<@NotNull Shift> getActiveShifts() {
        List<Shift> allShifts = getShifts();
        List<Shift> activeShifts = new ArrayList<>();
        for (Shift shift : allShifts) {
            if (shift.getEndedAt() == null) {
                activeShifts.add(shift);
            }
        }
        return activeShifts;
    }
}
