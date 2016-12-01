package ru.mail.park.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.model.UserDao;
import ru.mail.park.model.UserDaoImpl;
import ru.mail.park.model.UserProfile;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    private final UserDao userDao;

    @Autowired
    public AccountServiceImpl(DataBaseService dataBaseService) {
        userDao = new UserDaoImpl(dataBaseService.getJdbcTemplate());
    }

    @Override
    public void addUser(String login, String password, String email) {
        userDao.create(new UserProfile(login, email, password));
    }

    @Override
    public UserProfile getUserByLogin(String login) {
        return userDao.getByLogin(login);
    }

    @Override
    public List<UserProfile> getTopRanked(int limit) {
        return userDao.getTopRanked(limit);
    }

    @Override
    public void updateUser(UserProfile userProfile) {
        userDao.update(userProfile);
    }

    @Transactional
    @Override
    public void updateUsers(List<UserProfile> userProfiles) {
        for (UserProfile userProfile : userProfiles) {
            userDao.update(userProfile);
        }
    }
}
