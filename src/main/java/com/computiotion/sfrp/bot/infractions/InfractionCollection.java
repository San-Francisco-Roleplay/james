package com.computiotion.sfrp.bot.infractions;

import com.computiotion.sfrp.bot.DatabaseSaveable;
import com.computiotion.sfrp.bot.Generators;
import com.google.gson.Gson;
import redis.clients.jedis.Jedis;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static com.computiotion.sfrp.bot.ConfigManager.REDIS_PREFIX_INFRACTION_COLLECTION;
import static com.computiotion.sfrp.bot.ConfigManager.REDIS_PREFIX_INFRACTION_HISTORY;

public class InfractionCollection implements DatabaseSaveable {
    private static final Jedis jedis = Generators.getJedis();
    private static final Gson gson = Generators.getGson();

    private List<String> userId;
    private transient String id;
    private Set<Infraction> inractions;
    private Instant at;

    public static InfractionCollection getCollection(String id) {
        String dataStr = jedis.get(REDIS_PREFIX_INFRACTION_COLLECTION + id);
        if (dataStr == null) return null;

        return gson.fromJson(dataStr, InfractionCollection.class);
    }

    @Override
    public void save() {
        jedis.set(REDIS_PREFIX_INFRACTION_COLLECTION + userId, gson.toJson(this));
    }
}
