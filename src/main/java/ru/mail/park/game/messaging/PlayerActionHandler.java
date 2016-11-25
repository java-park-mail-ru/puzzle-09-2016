package ru.mail.park.game.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.mail.park.game.GameMechService;
import ru.mail.park.model.UserProfile;
import ru.mail.park.websocket.HandleException;
import ru.mail.park.websocket.MessageHandler;
import ru.mail.park.websocket.MessageHandlerService;

import javax.annotation.PostConstruct;

@Component
public class PlayerActionHandler extends MessageHandler<PlayerAction> {
    private MessageHandlerService messageHandlerService;
    private GameMechService gameMechService;

    @Autowired
    public PlayerActionHandler(MessageHandlerService messageHandlerService, GameMechService gameMechService) {
        super(PlayerAction.class);
        this.messageHandlerService = messageHandlerService;
        this.gameMechService = gameMechService;
    }

    @PostConstruct
    private void init() {
        messageHandlerService.registerHandler(PlayerAction.class, this);
    }

    @Override
    public void handle(PlayerAction message, UserProfile userProfile) throws HandleException {
        gameMechService.addPlayerAction(userProfile, message);
    }
}
