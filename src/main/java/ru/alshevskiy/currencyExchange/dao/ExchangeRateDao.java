package ru.alshevskiy.currencyExchange.dao;

import ru.alshevskiy.currencyExchange.exception.DaoException;
import ru.alshevskiy.currencyExchange.entity.ExchangeRate;
import ru.alshevskiy.currencyExchange.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDao implements Dao<Long, ExchangeRate> {
    private static final ExchangeRateDao INSTANCE = new ExchangeRateDao();
    private static final String DELETE_SQL = """
                                            DELETE
                                            FROM exchangeRates
                                            WHERE id = ?
                                            """;
    private static final String UPDATE_SQL = """
                                            UPDATE exchangeRates
                                            SET rate = ?
                                            WHERE id = ?
                                            """;
    private static final String FIND_ALL_SQL = """
            SELECT id, base_currency_id, target_currency_id, rate
            FROM exchangeRates
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT id, base_currency_id, target_currency_id, rate
            FROM exchangeRates
            WHERE id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO exchangeRates
            VALUES (base_currency_id = ?, target_currency_id= ?, rate = ?)
            """;



    public static ExchangeRateDao getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean delete(Long id) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(DELETE_SQL)) {

            statement.setLong(1, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(ExchangeRate exchangeRate) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(UPDATE_SQL)) {

            statement.setDouble(1, exchangeRate.getRate());
            statement.setLong(2, exchangeRate.getId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<ExchangeRate> findAll() {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {

            List<ExchangeRate> result = new ArrayList<>();

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(exchangeRateBuilder(resultSet));
            }

            return result;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<ExchangeRate> findById(Long id) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            ExchangeRate exchangeRate = null;

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())  {
                exchangeRate = exchangeRateBuilder(resultSet);
            }

            return Optional.ofNullable(exchangeRate);

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public ExchangeRate save(ExchangeRate exchangeRate) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(SAVE_SQL)) {

            statement.setLong(1, exchangeRate.getBaseCurrencyId());
            statement.setLong(2, exchangeRate.getTargetCurrencyId());
            statement.setDouble(3, exchangeRate.getRate());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                exchangeRate.setId(generatedKeys.getLong(1));
            }

            return exchangeRate;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private ExchangeRate exchangeRateBuilder(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
                resultSet.getLong("id"),
                resultSet.getLong("base_currency_id"),
                resultSet.getLong("target_currency_id"),
                resultSet.getDouble("rate")
        );
    }
}
