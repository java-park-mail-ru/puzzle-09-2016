package ru.mail.park.game;

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import ru.mail.park.game.mechanics.GameSession;
import ru.mail.park.game.mechanics.Player;
import ru.mail.park.game.mechanics.PlayerActionService;
import ru.mail.park.game.messaging.PlayerAction;
import ru.mail.park.game.messaging.ServerSnapService;
import ru.mail.park.model.UserProfile;
import ru.mail.park.services.AccountService;
import ru.mail.park.websocket.RemotePointService;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


@Service
public class GameMechService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final int WIN_RANK_GAIN = 25;
    private RemotePointService remotePointService;
    private PlayerActionService playerActionService;
    private ServerSnapService serverSnapService;
    private AccountService accountService;
    private Queue<UserProfile> queue = new ConcurrentLinkedQueue<>();
    private Set<UserProfile> players = new ConcurrentHashSet<>();
    private Set<GameSession> gameSessions = new ConcurrentHashSet<>();

    @Autowired
    public GameMechService(RemotePointService remotePointService, PlayerActionService playerActionService,
                           ServerSnapService serverSnapService, AccountService accountService) {
        this.remotePointService = remotePointService;
        this.playerActionService = playerActionService;
        this.serverSnapService = serverSnapService;
        this.accountService = accountService;
    }

    public void addPlayer(UserProfile userProfile) {
        if (!queue.contains(userProfile) && !players.contains(userProfile)) {
            queue.add(userProfile);
        }
    }

    public void addPlayerAction(UserProfile userProfile, PlayerAction action) {
        playerActionService.add(userProfile, action);
    }

    public void step() {
        for (GameSession session : gameSessions) {
            playerActionService.processActionsForSession(session);
        }
        final Iterator<GameSession> iterator = gameSessions.iterator();
        while (iterator.hasNext()) {
            final GameSession session = iterator.next();
            try {
                final Player winner = session.getWinner();
                if (winner != null) {
                    processGameOver(session, winner);
                    terminateSession(session);
                    iterator.remove();
                    serverSnapService.sendGameOverSnap(session, winner);
                } else {
                    serverSnapService.sendSnapForSession(session);
                }
            } catch (RuntimeException e) {
                logger.error("Sending snapshots failed, terminating the session", e);
                terminateSession(session);
                iterator.remove();
            }
        }
        startGames();
    }

    @Transactional
    private void processGameOver(GameSession session, Player winner) {
        final Player loser = winner.equals(session.getFirst()) ? session.getSecond() : session.getFirst();
        final UserProfile winnerProfile = winner.getUserProfile();
        final UserProfile loserProfile = loser.getUserProfile();
        winnerProfile.setRank(winnerProfile.getRank() + WIN_RANK_GAIN);
        loserProfile.setRank(loserProfile.getRank() - WIN_RANK_GAIN);
        accountService.updateUser(winnerProfile);
        accountService.updateUser(loserProfile);
    }

    public void reset() {
        for (GameSession session : gameSessions) {
            terminateSession(session);
        }
        gameSessions.clear();
    }

    private void startGames() {
        final List<UserProfile> list = new ArrayList<>();
        while (!queue.isEmpty()) {
            final UserProfile userProfile = queue.poll();
            if (isConnected(userProfile)) {
                list.add(userProfile);
            }
        }
        if (list.size() % 2 == 1) {
            final int lastIndex = list.size() - 1;
            final UserProfile last = list.get(lastIndex);
            list.remove(lastIndex);
            queue.add(last);
        }
        players.addAll(list);
        for (int i = 0; i < list.size(); i += 2) {
            final GameSession session = new GameSession(new Player(list.get(i)), new Player(list.get(i + 1)));
            gameSessions.add(session);
        }
    }

    private boolean isConnected(UserProfile userProfile) {
        return remotePointService.isConnected(userProfile);
    }

    private void terminateSession(GameSession session) {
        remotePointService.cutDownConnection(session.getFirst().getUserProfile(), CloseStatus.SERVER_ERROR);
        remotePointService.cutDownConnection(session.getSecond().getUserProfile(), CloseStatus.SERVER_ERROR);
        players.remove(session.getFirst().getUserProfile());
        players.remove(session.getSecond().getUserProfile());
    }
}
