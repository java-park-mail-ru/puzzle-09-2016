package ru.mail.park.model;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.mail.park.model.exception.UserAlreadyExistsException;

import java.util.List;

public class UserDaoImpl extends BaseDao<UserProfile> implements UserDao {
    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public UserProfile getByLogin(String login) {
        final List<UserProfile> list = getJdbcTemplate().query("SELECT * FROM user_profile WHERE login = ?;",
                new UserRowMapper(), login);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<UserProfile> getTopRanked(int limit) {
        final String limitOperator = limit > 0 ? " LIMIT " + limit : "";
        return getJdbcTemplate().query("SELECT * FROM user_profile ORDER BY rank DESC" + limitOperator + ';',
                new UserRowMapper());
    }

    @Override
    public UserProfile get(UserProfile entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void create(UserProfile entity) {
        try {
            getJdbcTemplate().update("INSERT INTO user_profile (login, passwd, email) VALUES (?, ?, ?);",
                    entity.getLogin(), entity.getPassword(), entity.getEmail());
        } catch (DuplicateKeyException e) {
            throw new UserAlreadyExistsException(e);
        }
    }

    @Override
    public void update(UserProfile entity) {
        getJdbcTemplate().update("UPDATE user_profile SET rank = ? WHERE " +
                "login = ?;", entity.getRank(), entity.getLogin());
    }

    @Override
    public void delete(UserProfile entity) {
        throw new UnsupportedOperationException();
    }
}
