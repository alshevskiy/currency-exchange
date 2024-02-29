package ru.alshevskiy.currencyexchange.database;

import ru.alshevskiy.currencyexchange.util.ConnectionManager;
import ru.alshevskiy.currencyexchange.util.PropertiesUtil;

import java.sql.Connection;
import java.sql.SQLException;

public class TestRunner {
    public static void main(String[] args) throws SQLException {
        try (Connection connection = ConnectionManager.open()) {
//            System.out.println(connection.getTransactionIsolation());
        }
    }
}
