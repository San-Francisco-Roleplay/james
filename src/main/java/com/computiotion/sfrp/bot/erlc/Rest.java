package com.computiotion.sfrp.bot.erlc;


import com.computiotion.sfrp.bot.ConfigManager;
import com.google.gson.JsonObject;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.computiotion.sfrp.bot.Generators.getGson;

public class Rest {
    public static final String BASE_URL = "https://api.policeroleplay.community/v1";
    private static final Log log = LogFactory.getLog(Rest.class);

    private final String key;

    private Rest(String key) {
        this.key = key;
    }

    @Contract(" -> new")
    public static @NotNull Rest fromEnv() {
        return fromApiKey(ConfigManager.getERLCApiKey());
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull Rest fromApiKey(String key) {
        return new Rest(key);
    }

    public String getKey() {
        return key;
    }

    public boolean command(String command) throws IOException {
        if (!command.startsWith(":")) command = ":" + command;
        JsonObject json = new JsonObject();
        json.addProperty("command", command);

        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");
        headers.put("Content-Type", "application/json");
        headers.put("Server-Key", key);

        HttpResponse<JsonNode> response
                = Unirest.post(BASE_URL + "/server/command")
                .headers(headers)
                .body(getGson().toJson(json))
                .asJson();

        return response.getStatus() == 200;
    }

    public boolean hint(String text) throws IOException {
        return command(":h " + text);
    }

    public boolean message(String text) throws IOException {
        return command(":m " + text);
    }

    public boolean message(String target, String text) throws IOException {
        return command(":pm " + target + " " + text);
    }
}
