package ru.mail.park.model;

import org.springframework.dao.DuplicateKeyException;
import ru.mail.park.model.exception.UserAlreadyExistsException;
import ru.mail.park.services.DataBaseService;

import java.util.List;

public class UserDaoImpl implements UserDao {
    @Override
    public UserProfile getByLogin(String login) {
        List<UserProfile> list = DataBaseService.getJdbcTemplate().query("SELECT * FROM user_profile WHERE login = ?;",
                new UserRowMapper(), login);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<UserProfile> getTopRanked(int limit) {
        String limitOperator = limit > 0 ? " LIMIT " + limit : "";
        return DataBaseService.getJdbcTemplate().query("SELECT * FROM user_profile ORDER BY rank DESC" +
                limitOperator + ";", new UserRowMapper());
    }

    @Override
    public UserProfile get(UserProfile entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void create(UserProfile entity) {
        try {
            DataBaseService.getJdbcTemplate().update("INSERT INTO user_profile (login, passwd, email) " +
                    "VALUES (?, ?, ?);", entity.getLogin(), entity.getPassword(), entity.getEmail());
        } catch (DuplicateKeyException e) {
            throw new UserAlreadyExistsException(e);
        }
    }

    @Override
    public void update(UserProfile entity) {
        DataBaseService.getJdbcTemplate().update("UPDATE user_profile SET rank = ? WHERE " +
                "login = ?;", entity.getRank(), entity.getLogin());
    }

    @Override
    public void delete(UserProfile entity) {
        throw new UnsupportedOperationException();
    }
}
