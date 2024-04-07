package ru.alshevskiy.currencyExchange.service;

import ru.alshevskiy.currencyExchange.exception.ElementNotFoundException;
import ru.alshevskiy.currencyExchange.model.Currency;
import ru.alshevskiy.currencyExchange.repository.ExchangeRepository;
import ru.alshevskiy.currencyExchange.repository.JdbcExchangeRepository;
import ru.alshevskiy.currencyExchange.dto.ExchangeRateDto;
import ru.alshevskiy.currencyExchange.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ExchangeRateService {
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();

    private final ExchangeRepository exchangeRepository = JdbcExchangeRepository.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();

    private ExchangeRateService() {
    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }

    public List<ExchangeRate> findAll() throws SQLException, ElementNotFoundException {
        List<ExchangeRate> exchangeRateList = exchangeRepository.findAll();
        if (exchangeRateList.isEmpty()) {
            throw new ElementNotFoundException("Ни одного обменного курса не найдено");
        }

        return exchangeRateList;
    }

    public ExchangeRate findById(Long id) throws SQLException, ElementNotFoundException {
        Optional<ExchangeRate> optionalExchangeRate = exchangeRepository.findById(id);
        if (optionalExchangeRate.isEmpty())  {
            throw new ElementNotFoundException("Обменный курс не найден");
        }

        return optionalExchangeRate.get();
    }

    public ExchangeRate findByCodes(String baseCurrencyCode, String targetCurrencyCode) throws SQLException, ElementNotFoundException {
        Currency baseCurrency = getBaseCurrencyByCode(baseCurrencyCode);
        Currency targetCurrency = getTargetCurrencyByCode(targetCurrencyCode);

        Optional<ExchangeRate> optionalExchangeRate = exchangeRepository
                .findByBaseAndTargetCurrencyId(baseCurrency.getId(), targetCurrency.getId());
        if (optionalExchangeRate.isEmpty()) {
            throw new ElementNotFoundException("Обменный курс не найден");
        }

        return optionalExchangeRate.get();
    }

    public ExchangeRateDto save(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws SQLException, ElementNotFoundException {
        Currency baseCurrency = getBaseCurrencyByCode(baseCurrencyCode);
        Currency targetCurrency = getTargetCurrencyByCode(targetCurrencyCode);

        Long savedExchangeRateId = exchangeRepository.save(
                new ExchangeRate(null, baseCurrency.getId(), targetCurrency.getId(), rate)
        );

        return buildExchangeRateDto(savedExchangeRateId, baseCurrency, targetCurrency, rate);
    }

    public ExchangeRateDto updateByCurrencyPair(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws SQLException, ElementNotFoundException {
        Currency baseCurrency = getBaseCurrencyByCode(baseCurrencyCode);
        Currency targetCurrency = getTargetCurrencyByCode(targetCurrencyCode);

        Optional<ExchangeRate> optionalTargetExchangeRate = exchangeRepository
               .findByBaseAndTargetCurrencyId(baseCurrency.getId(), targetCurrency.getId());
        if (optionalTargetExchangeRate.isEmpty()) {
            throw new ElementNotFoundException("Обменный курс, который необходимо изменить, не найден");
        }

        ExchangeRate targetExchangeRate = optionalTargetExchangeRate.get();
        targetExchangeRate.setRate(rate);
        exchangeRepository.update(targetExchangeRate);

        return buildExchangeRateDto(
                targetExchangeRate.getId(),
                baseCurrency,
                targetCurrency,
                rate
        );
    }

    private ExchangeRateDto buildExchangeRateDto(Long id, Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
        return new ExchangeRateDto(
                id,
                baseCurrency,
                targetCurrency,
                rate
        );
    }

    private Currency getBaseCurrencyByCode(String baseCurrencyCode) throws SQLException, ElementNotFoundException {
        Currency baseCurrency;
        try {
            baseCurrency = currencyService.findByCode(baseCurrencyCode);
        } catch (ElementNotFoundException e) {
            throw new ElementNotFoundException("Базовая валюта обмена не найдена");
        }
        return baseCurrency;
    }

    private Currency getTargetCurrencyByCode(String targetCurrencyCode) throws SQLException, ElementNotFoundException {
        Currency targetCurrency;
        try {
            targetCurrency = currencyService.findByCode(targetCurrencyCode);
        } catch (ElementNotFoundException e) {
            throw new ElementNotFoundException("Целевая валюта обмена не найдена");
        }
        return targetCurrency;
    }
}
