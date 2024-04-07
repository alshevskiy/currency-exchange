package ru.alshevskiy.currencyExchange.repository;

import ru.alshevskiy.currencyExchange.exception.ElementNotFoundException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {
    List<T> findAll() throws SQLException;
    Optional<T> findById(Long id) throws SQLException, ElementNotFoundException;
    Long save(T entity) throws SQLException;
    void update(T entity) throws SQLException;
    void delete(Long id) throws SQLException;
}
