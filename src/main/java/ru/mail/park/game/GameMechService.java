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
                if (!isConnected(session.getFirst().getUser()) && !isConnected(session.getSecond().getUser())) {
                    terminateSession(session, CloseStatus.NORMAL);
                    iterator.remove();
                } else if (!isConnected(session.getFirst().getUser())) {
                    endGame(session, session.getSecond());
                    iterator.remove();
                } else if (!isConnected(session.getSecond().getUser())) {
                    endGame(session, session.getFirst());
                    iterator.remove();
                }
                final Player winner = session.getWinner();
                if (winner != null) {
                    endGame(session, winner);
                    iterator.remove();
                } else {
                    serverSnapService.sendSnapForSession(session);
                }
            } catch (RuntimeException e) {
                logger.error("Sending snapshots failed, terminating the session", e);
                terminateSession(session, CloseStatus.SERVER_ERROR);
                iterator.remove();
            }
        }
        startGames();
    }

    @Transactional
    private void endGame(GameSession session, Player winner) {
        final Player loser = winner.equals(session.getFirst()) ? session.getSecond() : session.getFirst();
        final UserProfile winnerProfile = winner.getUser();
        final UserProfile loserProfile = loser.getUser();
        winnerProfile.setRank(winnerProfile.getRank() + WIN_RANK_GAIN);
        loserProfile.setRank(loserProfile.getRank() - WIN_RANK_GAIN);
        accountService.updateUser(winnerProfile);
        accountService.updateUser(loserProfile);
        serverSnapService.sendGameOverSnap(session, winner);
        terminateSession(session, CloseStatus.NORMAL);
    }

    public void reset() {
        for (GameSession session : gameSessions) {
            terminateSession(session, CloseStatus.SERVER_ERROR);
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

    private void terminateSession(GameSession session, CloseStatus closeStatus) {
        remotePointService.cutDownConnection(session.getFirst().getUser(), closeStatus);
        remotePointService.cutDownConnection(session.getSecond().getUser(), closeStatus);
        players.remove(session.getFirst().getUser());
        players.remove(session.getSecond().getUser());
    }
}
