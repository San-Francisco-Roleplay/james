package com.computiotion.sfrp.bot.reference;

import com.computiotion.sfrp.bot.ConfigManager;
import com.computiotion.sfrp.bot.Generators;
import com.computiotion.sfrp.bot.Reference;
import com.google.common.base.Preconditions;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;

public final class ReferenceManager {
    private static final HashMap<String, Class<? extends ReferencePayload>> types = new HashMap<>();
    private static final HashMap<String, Method> handlers = new HashMap<>();
    private static final Log log = LogFactory.getLog(ReferenceManager.class);

    public static void registerClass(@NotNull Class<?> reg) {
        Method[] methods = reg.getMethods();
        for (Method method : methods) {
            ReferenceHandler handler = method.getAnnotation(ReferenceHandler.class);
            if (handler == null) continue;
            registerHandler(method);
        }
    }

    public static void registerHandler(@NotNull Method method) {
        log.debug("Registering RefHandler > " + method.getDeclaringClass().getName() + "#" + method.getName());
        ReferenceHandler handler = method.getAnnotation(ReferenceHandler.class);
        Preconditions.checkNotNull(handler, "Handlers must be annotated with ReferenceHandler");

        String id = handler.value();
        Preconditions.checkState(!handlers.containsKey(id), "A handler with this Id has already been registered.");

        Parameter[] parameters = method.getParameters();
        String error = "A handler may only have two parameters of type ReferenceData and String respectively, or a third of type Message.";
        Preconditions.checkState(parameters.length == 2 || parameters.length == 3, error);
        Preconditions.checkState(parameters[0].getType() == ReferenceData.class, error);
        Preconditions.checkState(parameters[1].getType() == String.class || parameters[1].getType() == Message.class, error);

        if (parameters.length == 3) Preconditions.checkState(parameters[2].getType() == Message.class, error);

        handlers.putIfAbsent(id, method);
    }

    public static void registerData(ReferenceData data) {
        registerType(data.getPayload().getClass());

        Generators.getJedis().set(ConfigManager.REDIS_PREFIX_REF + data.getMessageId(), Generators.getGson().toJson(data));
    }

    public static void registerType(Class<? extends ReferencePayload> ref) {
        Reference annotation = ref.getAnnotation(Reference.class);
        Preconditions.checkNotNull(annotation, "ReferencePayload must be annotated with Reference, on " + ref.getName());

        types.putIfAbsent(annotation.value(), ref);
    }

    public static Class<? extends ReferencePayload> getType(@NotNull String type) {
        return types.get(type);
    }

    public static void removeData(ReferenceData data) {
        Generators.getJedis().del(ConfigManager.REDIS_PREFIX_REF + data.getMessageId());

    }

    public static ReferenceData getData(String messageId) {
        String json = Generators.getJedis().get(ConfigManager.REDIS_PREFIX_REF + messageId);
        log.trace("JSON: " + json);
        if (json == null) {
            log.debug("JSON is null for reference.");
            return null;
        }

        log.trace("Returning.");
        return Generators.getGson().fromJson(json, ReferenceDataImpl.class);
    }

    public static Method getHandler(String id) {
        return handlers.get(id);
    }
}
