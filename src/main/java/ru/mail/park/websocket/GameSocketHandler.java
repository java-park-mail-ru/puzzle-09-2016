package ru.mail.park.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.mail.park.model.UserProfile;
import ru.mail.park.services.AccountService;

import javax.naming.AuthenticationException;
import java.io.IOException;

public class GameSocketHandler extends TextWebSocketHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private AccountService accountService;
    private RemotePointService remotePointService;
    private MessageHandlerService messageHandlerService;

    public GameSocketHandler(AccountService accountService, RemotePointService remotePointService,
                             MessageHandlerService messageHandlerService) {
        this.accountService = accountService;
        this.remotePointService = remotePointService;
        this.messageHandlerService = messageHandlerService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws AuthenticationException {
        final Object sessionLogin = session.getAttributes().get("login");
        if (sessionLogin == null) {
            throw new AuthenticationException("Only authenticated users are allowed to play the game");
        }
        final UserProfile userProfile = accountService.getUserByLogin(sessionLogin.toString());
        if (userProfile == null) {
            throw new AuthenticationException("Only authenticated users are allowed to play the game");
        }
        remotePointService.registerUser(userProfile, session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws AuthenticationException {
        final Object httpSessionLogin = session.getAttributes().get("login");
        if (httpSessionLogin == null) {
            throw new AuthenticationException("Only authenticated users are allowed to play the game");
        }
        final UserProfile userProfile = accountService.getUserByLogin(httpSessionLogin.toString());
        if (userProfile == null) {
            throw new AuthenticationException("Only authenticated users are allowed to play the game");
        }
        handleMessage(userProfile, message);
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    private void handleMessage(UserProfile userProfile, TextMessage text) {
        final Message message;
        try {
            message = objectMapper.readValue(text.getPayload(), Message.class);
        } catch (IOException e) {
            logger.error("wrong json format ", e);
            return;
        }
        try {
            messageHandlerService.handle(message, userProfile);
        } catch (HandleException e) {
            logger.error("Can't handle message of type " + message.getType() + " with content: " + message.getContent(),
                    e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
        logger.warn("Websocket transport problem", throwable);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws AuthenticationException {
        final Object httpSessionLogin = session.getAttributes().get("login");
        if (httpSessionLogin == null) {
            throw new AuthenticationException("Only authenticated users are allowed to play the game");
        }
        final UserProfile userProfile = accountService.getUserByLogin(httpSessionLogin.toString());
        if (userProfile == null) {
            throw new AuthenticationException("Only authenticated users are allowed to play the game");
        }
        remotePointService.removeUser(userProfile);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
