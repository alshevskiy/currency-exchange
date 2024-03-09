package ru.alshevskiy.currencyExchange.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<K, E> {
    boolean delete(K id);
    boolean update(E e);
    List<E> findAll();
    Optional<E> findById(K id);
    E save(E e);
}
