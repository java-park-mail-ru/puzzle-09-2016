package ru.mail.park.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.mail.park.model.UserProfile;
import ru.mail.park.websocket.*;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class PlayerActionHandler extends MessageHandler<PlayerAction> {
    private MessageHandlerContainer messageHandlerContainer;
    @Autowired
    private RemotePointService remotePointService;

    public PlayerActionHandler(MessageHandlerContainer messageHandlerContainer) {
        super(PlayerAction.class);
        this.messageHandlerContainer = messageHandlerContainer;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(PlayerAction.class, this);
    }

    @Override
    public void handle(PlayerAction message, UserProfile userProfile) throws HandleException {
        try {
            remotePointService.sendMessageToUser(userProfile, new Message(PlayerAction.class.getName(), "a"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
