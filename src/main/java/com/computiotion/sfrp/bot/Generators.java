package com.computiotion.sfrp.bot;

import com.computiotion.sfrp.bot.adapters.ComponentDataAdapter;
import com.computiotion.sfrp.bot.adapters.DurationAdapter;
import com.computiotion.sfrp.bot.adapters.InfractionTypeAdapter;
import com.computiotion.sfrp.bot.adapters.InstantAdapter;
import com.computiotion.sfrp.bot.components.ComponentData;
import com.computiotion.sfrp.bot.infractions.InfractionType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;

import java.time.Duration;
import java.time.Instant;

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
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(InfractionType.class, new InfractionTypeAdapter())
                .registerTypeAdapter(ComponentData.class, new ComponentDataAdapter())
                .create();
    }
}
