package ru.alshevskiy.currencyExchange.repository;

import ru.alshevskiy.currencyExchange.exception.ElementNotFoundException;
import ru.alshevskiy.currencyExchange.model.Currency;
import ru.alshevskiy.currencyExchange.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCurrencyRepository implements CurrencyRepository {
    private static final JdbcCurrencyRepository INSTANCE = new JdbcCurrencyRepository();

    private JdbcCurrencyRepository() {
    }

    public static JdbcCurrencyRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public List<Currency> findAll() throws SQLException {
        final String query = "SELECT id, code, full_name, sign FROM currencies";

        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            List<Currency> currencyList = new ArrayList<>();
            while (resultSet.next()) {
                currencyList.add(buildCurrency(resultSet));
            }

            return currencyList;
        }
    }

    @Override
    public Optional<Currency> findById(Long id) throws SQLException {
        final String query = "SELECT code, full_name, sign FROM currencies WHERE id = ?";

        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
               return Optional.empty();
            }

            return Optional.of(buildCurrency(resultSet));
        }
    }

    @Override
    public Optional<Currency> findByCode(String code) throws SQLException {
        final String query = "SELECT id, code, full_name, sign FROM currencies WHERE code = ?";

        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(query)) {
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return Optional.empty();
            }

            return Optional.of(buildCurrency(resultSet));
        }
    }

    @Override
    public Long save(Currency currency) throws SQLException {
        final String query = "INSERT INTO currencies(code, full_name, sign) VALUES (?, ?, ?)";

        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getName());
            statement.setString(3, currency.getSign());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();

            return generatedKeys.getLong(1);
        }
    }

    @Override
    public void update(Currency currency) throws SQLException {
        final String query = "UPDATE currencies SET code = ?, full_name = ?, sign = ? WHERE id = ?";

        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(query)) {

            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getName());
            statement.setString(3, currency.getSign());
            statement.setLong(4, currency.getId());

            statement.executeUpdate();
        }
    }

    @Override
    public void delete(Long id) throws SQLException {
        final String DELETE_SQL = "DELETE FROM currencies WHERE id = ?";

        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(DELETE_SQL)) {

            statement.setLong(1, id);
            statement.executeUpdate();
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