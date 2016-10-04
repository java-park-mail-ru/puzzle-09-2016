package ru.mail.park.services;

import org.springframework.stereotype.Service;
import ru.mail.park.model.UserProfile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountService {
    private Map<String, UserProfile> userNameToUser = new ConcurrentHashMap<>();

    public UserProfile addUser(String login, String password, String email) {
        final UserProfile user = new UserProfile(login, email, password);
        userNameToUser.put(login, user);
        return user;
    }

    public UserProfile getUserByLogin(String login) {
        return userNameToUser.get(login);
    }
}
