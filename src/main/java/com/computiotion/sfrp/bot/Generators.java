package com.computiotion.sfrp.bot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;

public class Generators {
    private static final Jedis jedis = createJedis();

    private static @NotNull Jedis createJedis() {
        Jedis jedis = new Jedis(ConfigManager.getRedisUrl(), ConfigManager.getRedisPort(), true);
        jedis.auth(ConfigManager.getRedisPassword());

        return jedis;
    }

    public static @NotNull Jedis getJedis() {
        return jedis;
    }

    public static @NotNull Gson getGson() {
        return new GsonBuilder()
                .create();
    }
}
