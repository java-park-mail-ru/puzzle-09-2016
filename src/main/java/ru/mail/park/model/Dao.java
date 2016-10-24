package ru.mail.park.model;

import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.mail.park.model.exception.DaoException;

public interface Dao<T extends BaseDaoEntity> {
    T get(T entity);
    void create(T entity);
    void update(T entity);
    void delete(T entity);
}
