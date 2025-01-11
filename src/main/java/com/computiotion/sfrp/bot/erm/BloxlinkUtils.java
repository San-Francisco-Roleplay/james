package com.computiotion.sfrp.bot.erm;

import com.computiotion.sfrp.bot.ConfigManager;
import com.computiotion.sfrp.bot.Hooks;
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
    private static final HashMap<String, String> discordToRoblox = new HashMap<>();
    private static final HashMap<String, String> robloxToDiscord = new HashMap<>();


    public static @Nullable String fromDiscord(String guild, String discordId) {
        if (discordToRoblox.containsKey(discordId)) return discordToRoblox.get(discordId);

        String url = String.format("https://api.blox.link/v4/public/guilds/%s/discord-to-roblox/%s", guild, discordId);

        HttpResponse<String> response = Unirest.get(url)
                .header("Authorization", ConfigManager.getBloxlinkApiKey())
                .asString();

        int status = response.getStatus();
        if (status == 404) return null;

        JsonObject body = Hooks.useBody(response.getBody());
        assert body != null;
        String robloxID = body.get("robloxID").getAsString();

        discordToRoblox.put(discordId, robloxID);
        robloxToDiscord.put(robloxID, discordId);

        return robloxID;
    }

    public static @Nullable String fromRoblox(String guild, String robloxId) {
        if (robloxToDiscord.containsKey(robloxId)) return robloxToDiscord.get(robloxId);

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

        discordToRoblox.put(discordId, robloxId);
        robloxToDiscord.put(robloxId, discordId);

        return discordId;
    }
}
