package ru.mail.park.game.mechanics;

import org.springframework.stereotype.Service;
import ru.mail.park.game.messaging.PlayerAction;
import ru.mail.park.model.UserProfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlayerActionService {
    private Map<UserProfile, List<PlayerAction>> userActions = new ConcurrentHashMap<>();

    public void add(UserProfile userProfile, PlayerAction action) {
        userActions.putIfAbsent(userProfile, new ArrayList<>());
        final List<PlayerAction> actions = userActions.get(userProfile);
        actions.add(action);
    }

    public void processActionsForSession(GameSession session) {
        final List<Player> players = new ArrayList<>();
        players.add(session.getFirst());
        players.add(session.getSecond());
        for (Player player : players) {
            for (PlayerAction action : getActionsForPlayer(player)) {
                processActionForPlayer(player, action);
            }
        }
    }

    private void processActionForPlayer(Player player, PlayerAction action) {
        if (action.isPositive()) {
            player.getSquare().activate(action.getRow(), action.getCol(), 2, 1);
        } else {
            player.getSquare().activate(action.getRow(), action.getCol(), -2, -1);
        }
    }

    private List<PlayerAction> getActionsForPlayer(Player player) {
        return userActions.getOrDefault(player.getUserProfile(), Collections.emptyList());
    }
}
