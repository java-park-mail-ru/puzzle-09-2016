package ru.mail.park.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.mail.park.model.UserProfile;

import java.io.IOException;

public abstract class MessageHandler<T> {
    private final Class<T> clazz;

    public MessageHandler(Class<T> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    public void handleMessage(Message message, UserProfile userProfile) throws HandleException {
        try {
            final Object data = new ObjectMapper().readValue(message.getContent(), clazz);
            handle(clazz.cast(data), userProfile);
        } catch (IOException | ClassCastException e) {
            throw new HandleException("Can't read incoming message of type " + message.getType() +
                    " with content: " + message.getContent(), e);
        }
    }

    public abstract void handle(T message, UserProfile userProfile) throws HandleException;
}
