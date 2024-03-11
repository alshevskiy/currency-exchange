package ru.alshevskiy.currencyExchange.service;

import ru.alshevskiy.currencyExchange.dao.ExchangeRateDao;
import ru.alshevskiy.currencyExchange.dto.ExchangeRateDto;

public class ExchangeRateService {
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();

    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();

    private ExchangeRateService() {
    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }

    public ExchangeRateDto findById(Long id) {
        return exchangeRateDao.findById(id).stream()
                .map(exchangeRate -> new ExchangeRateDto(
                        exchangeRate.getId(),
                        exchangeRate.getBaseCurrencyId(),
                        exchangeRate.getTargetCurrencyId(),
                        exchangeRate.getRate()
                        )
                )
                .findFirst().get();
    }

}
