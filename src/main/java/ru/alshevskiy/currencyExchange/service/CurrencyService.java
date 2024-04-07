package ru.alshevskiy.currencyExchange.service;

import ru.alshevskiy.currencyExchange.exception.ElementNotFoundException;
import ru.alshevskiy.currencyExchange.repository.CurrencyRepository;
import ru.alshevskiy.currencyExchange.repository.JdbcCurrencyRepository;
import ru.alshevskiy.currencyExchange.model.Currency;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();

    private final CurrencyRepository currencyRepository = JdbcCurrencyRepository.getInstance();

    private CurrencyService() {
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

    public List<Currency> findAll() throws SQLException {
        return currencyRepository.findAll();
    }

    public Currency findById(Long id) throws SQLException, ElementNotFoundException {
        Optional<Currency> optionalCurrency = currencyRepository.findById(id);
        if (optionalCurrency.isEmpty()) {
            throw new ElementNotFoundException("Валюта не найдена");
        }
        return optionalCurrency.get();
    }

    public Currency findByCode(String code) throws SQLException, ElementNotFoundException {
        Optional<Currency> optionalCurrency = currencyRepository.findByCode(code);
        if (optionalCurrency.isEmpty()) {
            throw new ElementNotFoundException("Валюта не найдена");
        }

        return optionalCurrency.get();
    }

    public Long save(String fullName, String code, String sign) throws SQLException {
        Currency newCurrency = new Currency(null, code, fullName, sign);
        return currencyRepository.save(newCurrency);
    }
}
