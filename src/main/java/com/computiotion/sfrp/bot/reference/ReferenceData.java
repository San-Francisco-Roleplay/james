package com.computiotion.sfrp.bot.reference;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface ReferenceData {
    /**
     * Retrieves the forward-to address.
     *
     * @return The forward-to address.
     */
    String getForwardTo();

    /**
     * Retrieves the method to forward to.
     *
     * @return The method to forward to.
     */
    Method forwardTo();

    String getMessageId();

    ReferencePayload getPayload();

    void execute(String message) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;
}
