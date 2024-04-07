package ru.alshevskiy.currencyExchange.repository;

import ru.alshevskiy.currencyExchange.exception.ElementNotFoundException;
import ru.alshevskiy.currencyExchange.model.Currency;

import java.sql.SQLException;
import java.util.Optional;

public interface CurrencyRepository extends CrudRepository<Currency> {
    Optional<Currency> findByCode(String code) throws SQLException, ElementNotFoundException;
}
