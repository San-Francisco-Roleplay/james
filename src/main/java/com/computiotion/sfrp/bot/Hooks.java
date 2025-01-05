package com.computiotion.sfrp.bot;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.Nullable;

import static com.computiotion.sfrp.bot.ConfigManager.getSetupPassword;

public class Hooks {
    public static @Nullable JsonObject useBody(String body) {
        JsonObject res;

        try {
            res = Generators.getGson().fromJson(body, JsonObject.class);
        } catch (JsonSyntaxException e) {
            return null;
        }

        return res;
    }

    public static boolean useAuth(String token) {
        if (token == null) return false;
        if (token.startsWith("Bearer ")) token = token.substring(7);

        return token.equals(getSetupPassword());
    }
}
