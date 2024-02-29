package main.java.ru.alshevskiy.currencyexchange.database;

import main.java.ru.alshevskiy.currencyexchange.util.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;

public class TestRunner {
    public static void main(String[] args) throws SQLException {
        try (Connection connection = ConnectionManager.open()) {
            System.out.println(connection.getTransactionIsolation());
        }
    }
}
