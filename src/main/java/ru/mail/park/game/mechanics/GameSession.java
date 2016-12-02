package ru.mail.park.game.mechanics;

import ru.mail.park.game.config.GameSettings;
import ru.mail.park.game.messaging.PlayerAction;
import ru.mail.park.model.UserProfile;

public class GameSession {
    private static final int TARGET_SCRAMBLE = GameSettings.getTargetScramble();
    private Player first;
    private Player second;
    private Square target;

    public GameSession(Player first, Player second) {
        this.first = first;
        this.second = second;
        target = new Square(TARGET_SCRAMBLE);
    }

    public Player getFirst() {
        return first;
    }

    public Player getSecond() {
        return second;
    }

    public Square getTarget() {
        return target;
    }

    public Player getPlayer(UserProfile userProfile) {
        if (first.getUser().equals(userProfile)) {
            return first;
        }
        if (second.getUser().equals(userProfile)) {
            return second;
        }
        return null;
    }

    public Player getOpponent(Player player) {
        if (first.equals(player)) {
            return second;
        }
        if (second.equals(player)) {
            return first;
        }
        return null;
    }

    public boolean contains(UserProfile userProfile) {
        return getPlayer(userProfile) != null;
    }

    public void processAction(Player player, PlayerAction action) {
        if (action.isPositive()) {
            player.getSquare().activate(action.getRow(), action.getCol(), 2, 1);
        } else {
            player.getSquare().activate(action.getRow(), action.getCol(), -2, -1);
        }
    }

    public boolean isWinner(Player player) {
        return player != null && player.getSquare().equals(target);
    }
}
