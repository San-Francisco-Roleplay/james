package com.computiotion.sfrp.bot.components;

import com.computiotion.sfrp.bot.Generators;
import com.computiotion.sfrp.bot.Snowflake;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;

import javax.swing.plaf.basic.BasicTreeUI;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;

import static com.computiotion.sfrp.bot.ConfigManager.REDIS_PREFIX_PERM_COMPONENT;

public class ComponentManager {
    private static final Jedis jedis = Generators.getJedis();
    private static final Gson gson = Generators.getGson();
    private static final Snowflake snowflake = new Snowflake(null, null, 1, null);
    private static final HashMap<String, ReferenceData> components = new HashMap<>();
    private static final HashMap<String, Method> methods = new HashMap<>();
    private static final HashMap<String, Class<? extends ComponentData>> types = new HashMap<>();

    public static void registerType(@NotNull Class<? extends ComponentData> type) {
        ComponentReference ref = type.getAnnotation(ComponentReference.class);
        Preconditions.checkNotNull(ref, "ComponentData must be annotated with ComponentReference");

        types.put(ref.value(), type);
    }

    public static void registerGeneric(@NotNull Method method) {
        ComponentHandler ref = method.getAnnotation(ComponentHandler.class);
        Preconditions.checkNotNull(ref, "Generic Handler must be annotated with ComponentHandler");

        methods.put(ref.value(), method);
    }

    public static Class<? extends ComponentData> getType(@NotNull String type) {
        return types.get(type);
    }


    public static String registerComponent(ReferenceData ref) {
        return registerComponent(ref, false);
    }

    public static String registerComponent(ReferenceData ref, boolean perm) {
        String snowflake = ComponentManager.snowflake.nextId();

        if (perm) {
            Method method = ref.getComponentMethod();
            Preconditions.checkNotNull(method, "The component method must not be null.");
            ComponentHandler handler = method.getAnnotation(ComponentHandler.class);

            components.put(snowflake, ref);
        }

        components.put(snowflake, ref);
        return snowflake;
    }

    public static ReferenceData fromId(String id) {
        return components.get(id);
    }

    public static @Nullable ReferenceData resolve(@NotNull String id) {
        if (!id.startsWith("P#")) components.get(id);

        id = id.substring("P#".length(), 2);

        String[] split = id.split("#", 2); // P#class@method#{"_type": "abc"}

        String ref = split[0];
        Method method = methods.get(ref);
        if (method == null) return null;

        String dataStr = jedis.get(REDIS_PREFIX_PERM_COMPONENT + split[1]);
        if (dataStr == null) return null;

        ComponentData payload = gson.fromJson(dataStr, ComponentData.class);

        return ReferenceData.generic(method, payload);
    }
}
