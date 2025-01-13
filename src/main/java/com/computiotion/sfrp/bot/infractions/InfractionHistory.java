package com.computiotion.sfrp.bot.infractions;

import com.computiotion.sfrp.bot.DatabaseSaveable;
import com.computiotion.sfrp.bot.Generators;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.computiotion.sfrp.bot.ConfigManager.REDIS_PREFIX_INFRACTION_HISTORY;

public class InfractionHistory implements DatabaseSaveable {
    private static final Jedis jedis = Generators.getJedis();
    private static final Gson gson = Generators.getGson();

    private transient String userId;
    private Set<String> infractions;

    private InfractionHistory() {}

    public static @Nullable InfractionHistory fromUserIdUnsafe(String userId) {
        String dataStr = jedis.get(REDIS_PREFIX_INFRACTION_HISTORY + userId);
        if (dataStr == null) return null;

        return gson.fromJson(dataStr, InfractionHistory.class);
    }

    public static @NotNull InfractionHistory fromUserId(String userId) {
        InfractionHistory history = fromUserIdUnsafe(userId);

        if (history == null) {
            history = new InfractionHistory();
        }

        history.userId = userId;
        history.save();

        return history;
    }

    @Override
    public void save() {
        jedis.set(REDIS_PREFIX_INFRACTION_HISTORY + userId, gson.toJson(this));
    }

    public Set<String> getInfractionIds() {
        if (infractions == null) infractions = new HashSet<>();
        return Collections.unmodifiableSet(infractions);
    }

    public Set<InfractionCollection> getInfractions() {
        Set<InfractionCollection> res = new HashSet<>();
        boolean edited = false;

        for (String id : getInfractionIds()) {
            InfractionCollection collection = InfractionCollection.getCollection(id);

            if (collection == null) {
                infractions.remove(id);
                edited = true;
                continue;
            }

            res.add(collection);
        }

        if (edited) save();

        return Collections.unmodifiableSet(res);
    }
}
