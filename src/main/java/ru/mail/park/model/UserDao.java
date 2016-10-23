package ru.mail.park.model;

public interface UserDao extends Dao<UserProfile> {
    UserProfile getByLogin(String login);
}
