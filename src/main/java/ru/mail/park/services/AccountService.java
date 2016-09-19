package ru.mail.park.services;

import org.springframework.stereotype.Service;
import ru.mail.park.model.UserProfile;

import java.util.HashMap;
import java.util.Map;

@Service
public class AccountService {
    private Map<String, UserProfile> userNameToUser = new HashMap<>();

    public UserProfile addUser(String login, String password, String email) {
        final UserProfile user = new UserProfile(login, email, password);
        userNameToUser.put(login, user);
        return user;
    }

    public UserProfile getUser(String login) {
        return userNameToUser.get(login);
    }
}
