package ru.alshevskiy.currencyExchange.service;

import ru.alshevskiy.currencyExchange.dao.CurrencyDao;
import ru.alshevskiy.currencyExchange.dto.CurrencyDto;

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
        return currencyDao.findAll().stream()
                .map(currency -> new CurrencyDto(
                    currency.getId(),
                    currency.getCode(),
                    currency.getFullName(),
                    currency.getSign()
                ))
                .toList();
    }

    public CurrencyDto findByCode(String code) {
        return currencyDao.findByCode(code).stream()
                .map(currency -> new CurrencyDto(
                        currency.getId(),
                        currency.getCode(),
                        currency.getFullName(),
                        currency.getSign()
                ))
                .toList().get(0);
    }
}
