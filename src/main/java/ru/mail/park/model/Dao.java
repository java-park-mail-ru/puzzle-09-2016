package ru.mail.park.model;

public interface Dao<T extends BaseDaoEntity> {
    T get(T entity);
    void create(T entity);
    void update(T entity);
    void delete(T entity);
}
