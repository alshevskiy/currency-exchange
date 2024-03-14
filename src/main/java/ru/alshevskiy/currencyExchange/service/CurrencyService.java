package ru.alshevskiy.currencyExchange.service;

import ru.alshevskiy.currencyExchange.dao.CurrencyDao;
import ru.alshevskiy.currencyExchange.dto.CurrencyDto;
import ru.alshevskiy.currencyExchange.entity.Currency;

import java.util.List;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();

    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private CurrencyService() {
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

    public List<CurrencyDto> findAll() {
        return currencyDao.findAll()
                .stream()
                .map(this::buildDto)
                .toList();
    }

    public CurrencyDto findById(Long id) {
        return buildDto(
                currencyDao.findById(id).orElseThrow());
    }

    public CurrencyDto findByCode(String code) {
        return buildDto(
                currencyDao.findByCode(code).orElseThrow()
        );
    }

    public CurrencyDto save(String fullName, String code, String sign) {
        Currency savedCurrency = currencyDao.save(
                new Currency(
                        null,
                        code,
                        fullName,
                        sign
                )
        );
        return buildDto(savedCurrency);
    }

    private CurrencyDto buildDto(Currency currency) {
        return new CurrencyDto(
                currency.getId(),
                currency.getCode(),
                currency.getFullName(),
                currency.getSign()
        );
    }
}
