package com.computiotion.sfrp.bot.reference;

import com.google.common.base.Preconditions;
import com.google.gson.annotations.SerializedName;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReferenceDataImpl implements ReferenceData {
    private static final Log log = LogFactory.getLog(ReferenceDataImpl.class);
    @SerializedName("forward_to")
    private final String forwardTo;
    @SerializedName("message_id")
    private final String messageId;
    private final ReferencePayload payload;

    public ReferenceDataImpl(Method forwardTo, String messageId, ReferencePayload payload) {
        ReferenceHandler handler = forwardTo.getAnnotation(ReferenceHandler.class);
        Preconditions.checkNotNull(handler, "ForwardTo must be annotated with ReferenceHandler");
        this.forwardTo = handler.value();
        this.messageId = messageId;
        this.payload = payload;
    }

    public ReferenceDataImpl(String forwardTo, String messageId, ReferencePayload payload) {
        this.forwardTo = forwardTo;
        this.messageId = messageId;
        this.payload = payload;
    }

    @Override
    public String getForwardTo() {
        return forwardTo;
    }

    /**
     * Retrieves the method to forward to.
     *
     * @return The method to forward to.
     */
    @Override
    public Method forwardTo() {
        return ReferenceManager.getHandler(forwardTo);
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public ReferencePayload getPayload() {
        return payload;
    }

    @Override
    public void execute(Message repliedTo, Message message) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Method method = forwardTo();
        method.setAccessible(true);

        Constructor<?> constructor = method.getDeclaringClass().getConstructor();
        constructor.setAccessible(true);

        boolean isString = method.getParameters()[1].getType() == String.class;

        if (method.getParameterCount() == 3) {
            method.invoke(constructor.newInstance(), this, isString ? message.getContentRaw() : message, repliedTo);
            return;
        }
        method.invoke(constructor.newInstance(), this, isString ? message.getContentRaw() : message);
    }
}
