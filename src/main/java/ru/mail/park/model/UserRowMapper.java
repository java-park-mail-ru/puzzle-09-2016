package ru.mail.park.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements BaseDaoEntityRowMapper<UserProfile> {
    @Override
    public UserProfile mapRow(ResultSet resultSet, int i) throws SQLException {
        String login = resultSet.getString("login");
        String password = resultSet.getString("passwd");
        String email = resultSet.getString("email");
        int rank = resultSet.getInt("rank");
        return new UserProfile(login, email, password, rank);
    }
}
