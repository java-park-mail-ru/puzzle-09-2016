package ru.mail.park.services;

import org.springframework.stereotype.Service;
import ru.mail.park.model.UserDao;
import ru.mail.park.model.UserDaoImpl;
import ru.mail.park.model.UserProfile;

@Service
public class AccountService {
    private final UserDao userDao = new UserDaoImpl();

    public void addUser(String login, String password, String email) {
        userDao.create(new UserProfile(login, email, password));
    }

    public UserProfile getUserByLogin(String login) {
        return userDao.getByLogin(login);
    }
}
