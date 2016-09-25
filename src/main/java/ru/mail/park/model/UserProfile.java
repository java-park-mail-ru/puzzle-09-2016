package ru.mail.park.model;

import java.util.concurrent.atomic.AtomicLong;

public class UserProfile {
    private String login;
    private String email;
    private String password;
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
    private long id;

    public UserProfile(String login, String email, String password) {
        this.login = login;
        this.email = email;
        this.password = password;
        id = ID_GENERATOR.getAndIncrement();
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
