package com.computiotion.sfrp.bot;

import org.jetbrains.annotations.NotNull;

public class ConfigManager {
    /**
     * Retrieves the Discord bot token from {@code .env}.
     *
     * @return The token, as defined in the .env file.
     * @throws NullPointerException If the token {@code DISCORD_TOKEN} is not defined in the env file.
     */
    @NotNull
    public static String getBotToken() {
        String value = System.getenv("DISCORD_TOKEN");
        if (value == null) throw new NullPointerException("No DISCORD_TOKEN was found in the .env file.");
        return value;
    }

    /**
     * Retrieves the Redis URL from {@code .env}.
     *
     * @return The URL, as defined in the .env file.
     * @throws NullPointerException If the token {@code REDIS_URL} is not defined in the env file.
     */
    @NotNull
    public static String getRedisUrl() {
        String value = System.getenv("REDIS_URL");
        if (value == null) throw new NullPointerException("No REDIS_URL was found in the .env file.");
        return value;
    }

    /**
     * Retrieves the Redis password from {@code .env}.
     *
     * @return The URL, as defined in the .env file.
     * @throws NullPointerException If the token {@code REDIS_PASSWORD} is not defined in the env file.
     */
    @NotNull
    public static String getRedisPassword() {
        String value = System.getenv("REDIS_PASSWORD");
        if (value == null) throw new NullPointerException("No REDIS_PASSWORD was found in the .env file.");
        return value;
    }
}
