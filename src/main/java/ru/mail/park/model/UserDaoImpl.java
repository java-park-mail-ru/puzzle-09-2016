package ru.mail.park.model;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.mail.park.services.DataBaseService;

public class UserDaoImpl implements UserDao {
    @Override
    public UserProfile getByLogin(String login) {
        SqlRowSet set = DataBaseService.getJdbcTemplate().queryForRowSet("SELECT * FROM user_profile WHERE login = ?;",
                login);
        if (!set.next()) {
            return null;
        }
        return new UserProfile(login, set.getString("email"), set.getString("passwd"));
    }

    @Override
    public UserProfile get(UserProfile entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void create(UserProfile entity) {
        DataBaseService.getJdbcTemplate().update("INSERT INTO user_profile (login, passwd, email) VALUES (?, ?, ?);",
                entity.getLogin(), entity.getPassword(), entity.getEmail());
    }

    @Override
    public void update(UserProfile entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(UserProfile entity) {
        throw new UnsupportedOperationException();
    }
}
