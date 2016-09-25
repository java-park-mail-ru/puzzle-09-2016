package ru.mail.park.services;

import org.springframework.stereotype.Service;
import ru.mail.park.model.UserProfile;

import java.util.HashMap;
import java.util.Map;

@Service
public class AccountService {
    private Map<String, UserProfile> userNameToUser = new HashMap<>();
    private Map<String, UserProfile> sessionIdToUser = new HashMap<>();

    public UserProfile addUser(String login, String password, String email) {
        final UserProfile user = new UserProfile(login, email, password);
        userNameToUser.put(login, user);
        return user;
    }

    public void associateSessionIdWithUser(String sessionId, UserProfile user) {
        sessionIdToUser.put(sessionId, user);
    }

    public UserProfile getUserByLogin(String login) {
        return userNameToUser.get(login);
    }
    public UserProfile getUserBySessionId(String sessionId) {
        return sessionIdToUser.get(sessionId);
    }
}
