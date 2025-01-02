package com.computiotion.sfrp.bot;

import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;

public class Generators {
    public static @NotNull Jedis getJedis() {
        Jedis jedis = new Jedis(ConfigManager.getRedisUrl(), 34008, true);
        jedis.auth(ConfigManager.getRedisPassword());

        return jedis;
    }
}
