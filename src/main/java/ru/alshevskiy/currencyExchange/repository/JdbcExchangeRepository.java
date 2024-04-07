package ru.alshevskiy.currencyExchange.repository;

import ru.alshevskiy.currencyExchange.exception.ElementNotFoundException;
import ru.alshevskiy.currencyExchange.model.Currency;
import ru.alshevskiy.currencyExchange.model.ExchangeRate;
import ru.alshevskiy.currencyExchange.util.ConnectionManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class JdbcExchangeRepository implements ExchangeRepository {
    private static final JdbcExchangeRepository INSTANCE = new JdbcExchangeRepository();

    public static JdbcExchangeRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public List<ExchangeRate> findAll() throws SQLException {
        final String FIND_ALL_SQL = "SELECT id, base_currency_id, target_currency_id, rate FROM exchangeRates";

        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();

            List<ExchangeRate> exchangeRateList = new ArrayList<>();
            while (resultSet.next()) {
                exchangeRateList.add(
                        exchangeRateBuilder(resultSet)
                );
            }

            return exchangeRateList;
        }
    }

    @Override
    public Optional<ExchangeRate> findById(Long id) throws SQLException, ElementNotFoundException {
        final String FIND_BY_ID_SQL = "SELECT id, base_currency_id, target_currency_id, rate FROM exchangeRates WHERE id = ?";

        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next())  {
                return Optional.empty();
            }

            return Optional.of(exchangeRateBuilder(resultSet));
        }
    }

    @Override
    public Long save(ExchangeRate exchangeRate) throws SQLException {
        final String query = "INSERT INTO exchangeRates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";

        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, exchangeRate.getBaseCurrencyId());
            statement.setLong(2, exchangeRate.getTargetCurrencyId());
            statement.setBigDecimal(3, exchangeRate.getRate());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            return generatedKeys.getLong(1);
        }
    }

    @Override
    public void update(ExchangeRate exchangeRate) throws SQLException {
        final String query = " UPDATE exchangeRates SET rate = ? WHERE id = ?";

        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(query)) {

            statement.setBigDecimal(1, exchangeRate.getRate());
            statement.setLong(2, exchangeRate.getId());

            statement.executeUpdate();
        }
    }

    @Override
    public void delete(Long id) throws SQLException {
        final String query = "DELETE FROM exchangeRates WHERE id = ?";

        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);

            statement.executeUpdate();
        }
    }

    public Optional<ExchangeRate> findByBaseAndTargetCurrencyId(Long baseCurrencyId, Long targetCurrencyId) throws SQLException {
        final String query = "SELECT id, base_currency_id, target_currency_id, rate FROM exchangeRates WHERE base_currency_id = ? AND target_currency_id = ?";

        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(query)) {

            statement.setLong(1, baseCurrencyId);
            statement.setLong(2, targetCurrencyId);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return Optional.empty();
            }

            return Optional.of(exchangeRateBuilder(resultSet));
        }
    }

    private ExchangeRate exchangeRateBuilder(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
                resultSet.getLong("id"),
                resultSet.getLong("base_currency_id"),
                resultSet.getLong("target_currency_id"),
                resultSet.getBigDecimal("rate")
        );
    }
}