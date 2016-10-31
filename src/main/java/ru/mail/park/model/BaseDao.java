package ru.mail.park.model;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class BaseDao<T extends BaseDaoEntity> implements AbstractDao<T> {
    private final JdbcTemplate jdbcTemplate;

    protected BaseDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
