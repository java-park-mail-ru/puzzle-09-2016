package ru.mail.park.game;

import org.springframework.stereotype.Service;
import ru.mail.park.game.messaging.PlayerAction;
import ru.mail.park.model.UserProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlayerActionService {
    private Map<UserProfile, List<PlayerAction>> playerActions = new ConcurrentHashMap<>();

    public void add(UserProfile userProfile, PlayerAction action) {
        playerActions.putIfAbsent(userProfile, new ArrayList<>());
        final List<PlayerAction> actions = playerActions.get(userProfile);
        actions.add(action);
    }


}
