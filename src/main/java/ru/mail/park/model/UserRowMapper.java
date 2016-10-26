package ru.mail.park.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements BaseDaoEntityRowMapper<UserProfile> {
    @Override
    public UserProfile mapRow(ResultSet resultSet, int i) throws SQLException {
        final String login = resultSet.getString("login");
        final String password = resultSet.getString("passwd");
        final String email = resultSet.getString("email");
        final int rank = resultSet.getInt("rank");
        return new UserProfile(login, email, password, rank);
    }
}
