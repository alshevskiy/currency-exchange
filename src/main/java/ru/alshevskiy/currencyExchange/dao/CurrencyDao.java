package ru.alshevskiy.currencyExchange.dao;

import ru.alshevskiy.currencyExchange.exception.DaoException;
import ru.alshevskiy.currencyExchange.entity.Currency;
import ru.alshevskiy.currencyExchange.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao implements Dao<Long, Currency> {
    private static final CurrencyDao INSTANCE = new CurrencyDao();
    private static final String DELETE_SQL = """
                                            DELETE
                                            FROM currencies
                                            WHERE id = ?
                                            """;
    private static final String UPDATE_SQL = """
                                            UPDATE currencies
                                            SET code = ?, full_name = ?, sign = ?
                                            WHERE id = ?
                                            """;
    private static final String FIND_BY_ID_SQL = """
                                                SELECT code, full_name, sign
                                                FROM currencies
                                                WHERE id = ?
                                                """;
    private static final String SAVE_SQL = """
                                            INSERT INTO currencies(code, full_name, sign)
                                            VALUES (?, ?, ?)
                                            """;
    private static final String FIND_ALL_SQL = """
                                            SELECT id, code, full_name, sign
                                            FROM currencies
                                            """;
    private static final String FIND_BY_CODE_SQL = FIND_ALL_SQL + "WHERE code = ?";


    private CurrencyDao() {
    }

    public static CurrencyDao getInstance() {
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
    public boolean update(Currency currency) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(UPDATE_SQL)) {

            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());
            statement.setLong(4, currency.getId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Currency> findAll() {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {

            List<Currency> currencies = new ArrayList<>();

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                currencies.add(
                        buildCurrency(resultSet)
                );
            }

            return currencies;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Currency> findById(Long id) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            Currency currency = null;

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                currency = new Currency();
                currency.setId(id);
                currency.setCode(resultSet.getString("code"));
                currency.setFullName(resultSet.getString("full_name"));
                currency.setSign(resultSet.getString("sign"));
            }

            return Optional.ofNullable(currency);

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Optional<Currency> findByCode(String code) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_BY_CODE_SQL)) {

            Currency currency = null;

            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                currency = buildCurrency(resultSet);
            }

            return Optional.ofNullable(currency);

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Currency save(Currency currency) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                currency.setId(generatedKeys.getLong(1));
            }

            return currency;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private static Currency buildCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getLong("id"),
                resultSet.getString("code"),
                resultSet.getString("full_name"),
                resultSet.getString("sign")
        );
    }
}
