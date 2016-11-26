package ru.mail.park.game.mechanics;

public class GameSession {
    private Player first;
    private Player second;
    private Square target;

    public GameSession() {
        target = new Square();
        target.scramble(8);
    }

    public GameSession(Player first, Player second) {
        this.first = first;
        this.second = second;
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

    public Player getWinner() {
        if (first.getSquare().equals(target)) {
            return first;
        }
        if (second.getSquare().equals(target)) {
            return second;
        }
        return null;
    }
}
