package ru.mail.park.game.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mail.park.game.mechanics.GameSession;
import ru.mail.park.game.mechanics.Player;
import ru.mail.park.websocket.Message;
import ru.mail.park.websocket.RemotePointService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServerSnapService {
    private RemotePointService remotePointService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ServerSnapService(RemotePointService remotePointService) {
        this.remotePointService = remotePointService;
    }

    public void sendSnapForSession(GameSession session) {
        sendSnap(session, false, null);
    }

    public void sendGameOverSnap(GameSession session, Player winner) {
        sendSnap(session, true, winner.getUserProfile().getLogin());
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    private void sendSnap(GameSession session, boolean gameOver, String winner) {
        final ServerSnap snap = new ServerSnap();
        snap.setFirstPlayer(session.getFirst().getUserProfile().getLogin());
        snap.setSecondPlayer(session.getSecond().getUserProfile().getLogin());
        snap.setFirstMatrix(session.getFirst().getSquare().getMatrix());
        snap.setSecondMatrix(session.getSecond().getSquare().getMatrix());
        snap.setTarget(session.getTarget().getMatrix());
        snap.setGameOver(gameOver);
        snap.setWinner(winner);
        final List<Player> players = new ArrayList<>();
        players.add(session.getFirst());
        players.add(session.getSecond());
        try {
            final Message message = new Message(ServerSnap.class.getName(), objectMapper.writeValueAsString(snap));
            for (Player player : players) {
                remotePointService.sendMessageToUser(player.getUserProfile(), message);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed sending snapshot", e);
        }
    }
}
