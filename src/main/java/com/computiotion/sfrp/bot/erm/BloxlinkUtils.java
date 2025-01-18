package com.computiotion.sfrp.bot.erm;

import com.computiotion.sfrp.bot.ConfigManager;
import com.computiotion.sfrp.bot.Hooks;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class BloxlinkUtils {
    private static final Log log = LogFactory.getLog(BloxlinkUtils.class);
    // left is discord, right is roblox
    private static final BiMap<String, String> ids = HashBiMap.create();


    public static @Nullable String fromDiscord(String guild, String discordId) {
        if (ids.containsKey(discordId)) return ids.get(discordId);

        String url = String.format("https://api.blox.link/v4/public/guilds/%s/discord-to-roblox/%s", guild, discordId);

        HttpResponse<String> response = Unirest.get(url)
                .header("Authorization", ConfigManager.getBloxlinkApiKey())
                .asString();

        int status = response.getStatus();
        if (status == 404) return null;

        JsonObject body = Hooks.useBody(response.getBody());
        assert body != null;
        String robloxID = body.get("robloxID").getAsString();

        ids.put(discordId, robloxID);

        return robloxID;
    }

    public static @Nullable String fromRoblox(String guild, String robloxId) {
        if (ids.inverse().containsKey(robloxId)) return ids.inverse().get(robloxId);

        String url = String.format("https://api.blox.link/v4/public/guilds/%s/roblox-to-discord/%s", guild, robloxId);

        HttpResponse<String> response = Unirest.get(url)
                .header("Authorization", ConfigManager.getBloxlinkApiKey())
                .asString();

        int status = response.getStatus();
        if (status == 404) return null;

        JsonObject body = Hooks.useBody(response.getBody());
        assert body != null;
        List<JsonElement> discordIDs = body.get("discordIDs").getAsJsonArray().asList();
        if (discordIDs.isEmpty()) return null;

        String discordId = discordIDs.getFirst().getAsString();

        ids.put(robloxId, discordId);

        return discordId;
    }
}
