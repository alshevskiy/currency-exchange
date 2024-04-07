package ru.alshevskiy.currencyExchange.repository;

import ru.alshevskiy.currencyExchange.exception.ElementNotFoundException;
import ru.alshevskiy.currencyExchange.model.ExchangeRate;

import java.sql.SQLException;
import java.util.Optional;

public interface ExchangeRepository extends CrudRepository<ExchangeRate> {
    Optional<ExchangeRate> findByBaseAndTargetCurrencyId(Long baseCurrencyId, Long targetCurrencyId) throws SQLException, ElementNotFoundException;
}