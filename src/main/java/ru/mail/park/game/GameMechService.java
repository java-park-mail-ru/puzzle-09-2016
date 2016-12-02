package ru.mail.park.game;

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import ru.mail.park.game.config.GameSettings;
import ru.mail.park.game.mechanics.GameSession;
import ru.mail.park.game.mechanics.Player;
import ru.mail.park.game.messaging.PlayerAction;
import ru.mail.park.game.messaging.ServerSnapService;
import ru.mail.park.model.UserProfile;
import ru.mail.park.services.AccountService;
import ru.mail.park.websocket.RemotePointService;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


@Service
public class GameMechService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final int RANK_BOUNTY = GameSettings.getRankBounty();
    private RemotePointService remotePointService;
    private ServerSnapService serverSnapService;
    private AccountService accountService;
    private Queue<UserProfile> queue = new ConcurrentLinkedQueue<>();
    private Set<UserProfile> players = new ConcurrentHashSet<>();
    private Set<GameSession> gameSessions = new ConcurrentHashSet<>();

    @Autowired
    public GameMechService(RemotePointService remotePointService,ServerSnapService serverSnapService,
                           AccountService accountService) {
        this.remotePointService = remotePointService;
        this.serverSnapService = serverSnapService;
        this.accountService = accountService;
    }

    public void addPlayer(UserProfile userProfile) {
        if (!queue.contains(userProfile) && !players.contains(userProfile)) {
            queue.add(userProfile);
            startGames();
        }
    }

    public void addPlayerAction(UserProfile userProfile, PlayerAction action) {
        gameSessions.stream().filter(session -> session.contains(userProfile)).findAny().
                ifPresent(session -> processAction(action, userProfile, session));
    }

    private void startGames() {
        queue.removeIf(userProfile -> !isConnected(userProfile));
        while (queue.size() >= 2) {
            final UserProfile first = queue.poll();
            final UserProfile second = queue.poll();
            players.add(first);
            players.add(second);
            gameSessions.add(new GameSession(new Player(first), new Player(second)));
        }
    }

    private void processAction(PlayerAction action, UserProfile userProfile, GameSession session) {
        final Player player = session.getPlayer(userProfile);
        session.processAction(player, action);
        if (session.isWinner(player) || !isConnected(session.getOpponent(player).getUser())) {
            endGame(session, player);
        } else {
            try {
                serverSnapService.sendSnapsForSession(session);
            } catch (IOException e) {
                logger.error("failed to send server snaps", e);
                terminateSession(session, CloseStatus.NORMAL);
            }
        }
    }

    private boolean isConnected(UserProfile userProfile) {
        return remotePointService.isConnected(userProfile);
    }

    private void endGame(GameSession session, Player winner) {
        final UserProfile winnerProfile = winner.getUser();
        final UserProfile loserProfile = session.getOpponent(winner).getUser();
        winnerProfile.setRank(winnerProfile.getRank() + RANK_BOUNTY);
        loserProfile.setRank(loserProfile.getRank() - RANK_BOUNTY);
        final List<UserProfile> userProfiles = new ArrayList<>();
        userProfiles.add(winnerProfile);
        userProfiles.add(loserProfile);
        accountService.updateUsers(userProfiles);
        try {
            serverSnapService.sendGameOverSnaps(session, winner);
        } catch (IOException e) {
            logger.error("failed to send game over snaps", e);
        } finally {
            terminateSession(session, CloseStatus.NORMAL);
        }
    }

    private void terminateSession(GameSession session, CloseStatus closeStatus) {
        remotePointService.cutDownConnection(session.getFirst().getUser(), closeStatus);
        remotePointService.cutDownConnection(session.getSecond().getUser(), closeStatus);
        players.remove(session.getFirst().getUser());
        players.remove(session.getSecond().getUser());
    }
}
