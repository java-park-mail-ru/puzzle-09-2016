package ru.mail.park.game.mechanics;

import ru.mail.park.model.UserProfile;

public class Player {
    private UserProfile user;
    private Square square = new Square();

    public Player(UserProfile user) {
        this.user = user;
    }

    public UserProfile getUser() {
        return user;
    }

    public Square getSquare() {
        return square;
    }
}
