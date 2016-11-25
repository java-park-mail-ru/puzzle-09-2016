package ru.mail.park.game;

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mail.park.game.messaging.PlayerAction;
import ru.mail.park.model.UserProfile;
import ru.mail.park.services.AccountService;
import ru.mail.park.websocket.RemotePointService;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;


@Service
public class GameMechService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private RemotePointService remotePointService;
    private AccountService accountService;
    private PlayerActionService playerActionService;
    private Queue<UserProfile> queue = new ConcurrentLinkedQueue<>();
    private Set<UserProfile> players = new ConcurrentHashSet<>();
    private Set<GameSession> gameSessions = new ConcurrentHashSet<>();

    @Autowired
    public GameMechService(RemotePointService remotePointService, AccountService accountService,
                           PlayerActionService playerActionService) {
        this.remotePointService = remotePointService;
        this.accountService = accountService;
        this.playerActionService = playerActionService;
    }

    public void addPlayer(UserProfile userProfile) {
        if (!queue.contains(userProfile) && !players.contains(userProfile)) {
            queue.add(userProfile);
        }
    }

    public void addPlayerAction(UserProfile userProfile, PlayerAction action) {
        playerActionService.add(userProfile, action);
    }
}
