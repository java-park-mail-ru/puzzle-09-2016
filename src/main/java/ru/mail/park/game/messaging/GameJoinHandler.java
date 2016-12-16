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
public class GameJoinHandler extends MessageHandler<GameJoin> {
    private MessageHandlerService messageHandlerService;
    private GameMechService gameMechService;

    @Autowired
    public GameJoinHandler(MessageHandlerService messageHandlerService, GameMechService gameMechService) {
        super(GameJoin.class);
        this.messageHandlerService = messageHandlerService;
        this.gameMechService = gameMechService;
    }

    @PostConstruct
    private void init() {
        messageHandlerService.registerHandler(GameJoin.class, this);
    }

    @Override
    public void handle(GameJoin message, UserProfile userProfile) throws HandleException {
        gameMechService.addPlayer(userProfile);
    }
}
