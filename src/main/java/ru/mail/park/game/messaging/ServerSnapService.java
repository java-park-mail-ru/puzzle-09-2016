package ru.mail.park.game.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mail.park.game.mechanics.GameSession;
import ru.mail.park.game.mechanics.Player;
import ru.mail.park.websocket.Message;
import ru.mail.park.websocket.RemotePointService;

import java.io.IOException;

@Service
public class ServerSnapService {
    private RemotePointService remotePointService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ServerSnapService(RemotePointService remotePointService) {
        this.remotePointService = remotePointService;
    }

    public void sendSnapsForSession(GameSession session) {
        sendSnapsForSession(session, false, null);
    }

    public void sendGameOverSnaps(GameSession session, Player winner) {
        sendSnapsForSession(session, true, winner.getUser().getLogin());
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    private void sendSnapsForSession(GameSession session, boolean gameOver, String winner) {
        final Player first = session.getFirst();
        final Player second = session.getSecond();
        final ServerSnap firstSnap = createSnapForPlayer(first, session, gameOver, winner);
        final ServerSnap secondSnap = createSnapForPlayer(second, session, gameOver, winner);
        try {
            final Message firstMessage = new Message(ServerSnap.class.getName(),
                    objectMapper.writeValueAsString(firstSnap));
            final Message secondMessage = new Message(ServerSnap.class.getName(),
                    objectMapper.writeValueAsString(secondSnap));
            remotePointService.sendMessageToUser(first.getUser(), firstMessage);
            remotePointService.sendMessageToUser(second.getUser(), secondMessage);
        } catch (IOException e) {
            throw new RuntimeException("Failed sending snapshot", e);
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
