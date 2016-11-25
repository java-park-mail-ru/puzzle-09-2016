package ru.mail.park.game;

import ru.mail.park.model.UserProfile;

public class GameSession {
    private UserProfile first;
    private UserProfile second;

    public UserProfile getFirst() {
        return first;
    }

    public void setFirst(UserProfile first) {
        this.first = first;
    }

    public UserProfile getSecond() {
        return second;
    }

    public void setSecond(UserProfile second) {
        this.second = second;
    }
}
