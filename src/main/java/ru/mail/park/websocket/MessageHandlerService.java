package ru.mail.park.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.mail.park.model.UserProfile;

import java.util.HashMap;
import java.util.Map;

@Service
public class MessageHandlerService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    final Map<Class<?>, MessageHandler<?>> handlerMap = new HashMap<>();

    public void handle(Message message, UserProfile userProfile) throws HandleException {
        final Class clazz;
        try {
            clazz = Class.forName("ru.mail.park.game.messaging." + message.getType());
        } catch (ClassNotFoundException e) {
            throw new HandleException("Can't handle message of " + message.getType() + " type", e);
        }
        final MessageHandler<?> messageHandler = handlerMap.get(clazz);
        if (messageHandler == null) {
            throw new HandleException("no handler for message of " + message.getType() + " type");
        }
        messageHandler.handleMessage(message, userProfile);
        logger.debug("message handled: type =[" + message.getType() + "], content=[" + message.getContent() + ']');
    }

    public <T> void registerHandler(Class<T> clazz, MessageHandler<T> handler) {
        handlerMap.put(clazz, handler);
    }
}
