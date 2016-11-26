package ru.mail.park.game.mechanics;

import ru.mail.park.model.UserProfile;

public class Player {
    private UserProfile userProfile;
    private Square square = new Square();

    public Player(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public Square getSquare() {
        return square;
    }
}
