package com.computiotion.sfrp.bot.models;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

import static com.computiotion.sfrp.bot.Generators.getGson;
import static com.computiotion.sfrp.bot.Generators.getJedis;

public class Config {
    public static final String CONFIG_NAME = "config";
    private static final Log log = LogFactory.getLog(Config.class);
    private Set<String> prefixes = new HashSet<>(Set.of("$"));
    private static Config instance = null;

    private Config() {}

    public static Config getInstance() {
        if (instance != null) return instance;
        String configStr = getJedis().get(CONFIG_NAME);
        Config config;

        if (configStr == null) {
            config = new Config();
            getJedis().set(CONFIG_NAME, getGson().toJson(config));
        } else {
            config = getGson().fromJson(configStr, Config.class);
        }

        instance = config;
        return config;
    }

    @NotNull
    @UnmodifiableView
    public Set<String> getPrefixes() {
        return Collections.unmodifiableSet(prefixes);
    }

    public Config addPrefix(@NotNull String prefix) {
        prefixes.add(prefix);
        save();
        return this;
    }

    public Config removePrefix(String prefix) {
        prefixes.remove(prefix);
        save();
        return this;
    }

    public Config setPrefixes(Set<String> prefixes) {
        this.prefixes = prefixes;
        save();
        return this;
    }

    public Config save() {
        // Save the configuration to the database
        getJedis().set(CONFIG_NAME, getGson().toJson(this));
        return this;
    }
}
