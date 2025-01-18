package com.computiotion.sfrp.bot.reference;

import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;

public final class ReferenceManager {
    private static final HashMap<String, ReferenceData> data = new HashMap<>();
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
        String error = "A handler may only have two parameters of type ReferenceData and String respectively.";
        Preconditions.checkState(parameters.length == 2, error);
        Preconditions.checkState(parameters[0].getType() == ReferenceData.class, error);
        Preconditions.checkState(parameters[1].getType() == String.class, error);

        handlers.putIfAbsent(id, method);
    }

    public static void registerData(ReferenceData data) {
        ReferenceManager.data.put(data.getMessageId(), data);
    }

    public static ReferenceData getData(String messageId) {
        return data.get(messageId);
    }

    public static Method getHandler(String id) {
        return handlers.get(id);
    }
}
