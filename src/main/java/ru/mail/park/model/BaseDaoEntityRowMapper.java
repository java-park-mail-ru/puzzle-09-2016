package ru.mail.park.model;

import org.springframework.jdbc.core.RowMapper;

public interface BaseDaoEntityRowMapper<T extends BaseDaoEntity> extends RowMapper<T> {
}
