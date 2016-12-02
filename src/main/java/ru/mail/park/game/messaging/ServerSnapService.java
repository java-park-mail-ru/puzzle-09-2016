package ru.mail.park.game.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mail.park.game.mechanics.GameSession;
import ru.mail.park.game.mechanics.Player;
import ru.mail.park.websocket.Message;
import ru.mail.park.websocket.RemotePointService;

import java.io.IOException;

@Service
public class ServerSnapService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private RemotePointService remotePointService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ServerSnapService(RemotePointService remotePointService) {
        this.remotePointService = remotePointService;
    }

    public void sendSnapsForSession(GameSession session) throws IOException {
        sendSnapsForSession(session, false, null);
    }

    public void sendGameOverSnaps(GameSession session, Player winner) throws IOException {
        sendSnapsForSession(session, true, winner.getUser().getLogin());
    }

    @SuppressWarnings("OverlyBroadThrowsClause")
    private void sendSnapsForSession(GameSession session, boolean gameOver, String winner) throws IOException {
        final Player first = session.getFirst();
        final Player second = session.getSecond();
        final ServerSnap firstSnap = createSnapForPlayer(first, session, gameOver, winner);
        final ServerSnap secondSnap = createSnapForPlayer(second, session, gameOver, winner);
        final Message firstMsg = new Message(ServerSnap.class.getName(), objectMapper.writeValueAsString(firstSnap));
        final Message secondMsg = new Message(ServerSnap.class.getName(), objectMapper.writeValueAsString(secondSnap));
        IOException exception = null;
        try {
            remotePointService.sendMessageToUser(first.getUser(), firstMsg);
        } catch (IOException e) {
            logger.error("failed to send server snap to user " + first.getUser().getLogin(), e);
            exception = e;
        }
        try {
            remotePointService.sendMessageToUser(second.getUser(), secondMsg);
        } catch (IOException e) {
            logger.error("failed to send server snap to user " + second.getUser().getLogin(), e);
            exception = e;
        }
        if (exception != null) {
            throw exception;
        }
    }

    private ServerSnap createSnapForPlayer(Player player, GameSession session, boolean gameOver, String winner) {
        final ServerSnap snap = new ServerSnap();
        snap.setPlayer(player.getUser().getLogin());
        snap.setOpponent(session.getOpponent(player).getUser().getLogin());
        snap.setPlayerMatrix(player.getSquare().getMatrix());
        snap.setOpponentMatrix(session.getOpponent(player).getSquare().getMatrix());
        snap.setTarget(session.getTarget().getMatrix());
        snap.setGameOver(gameOver);
        if (gameOver) {
            snap.setWin(player.getUser().getLogin().equals(winner));
        }
        return snap;
    }
}
