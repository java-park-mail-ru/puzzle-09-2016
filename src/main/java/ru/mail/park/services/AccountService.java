package ru.mail.park.services;

import org.springframework.stereotype.Service;
import ru.mail.park.model.UserProfile;

import java.util.List;

@Service
public interface AccountService {
    void addUser(String login, String password, String email);
    UserProfile getUserByLogin(String login);
    List<UserProfile> getTopRanked(int limit);
    void updateUser(UserProfile userProfile);
    void updateUsers(List<UserProfile> userProfiles);
}
