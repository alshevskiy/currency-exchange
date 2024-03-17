package ru.alshevskiy.currencyExchange.service;

import ru.alshevskiy.currencyExchange.dao.CurrencyDao;
import ru.alshevskiy.currencyExchange.dao.ExchangeRateDao;
import ru.alshevskiy.currencyExchange.dto.ExchangeDto;
import ru.alshevskiy.currencyExchange.entity.Currency;

public class ExchangeService {
    private static final ExchangeService INSTANCE = new ExchangeService();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private ExchangeService() {
    }

    public static ExchangeService getInstance() {
        return INSTANCE;
    }

    public ExchangeDto exchange(String baseCurrencyCode, String targetCurrencyCode, Object amount) {
        Long usdId = currencyDao.findByCode("USD").orElseThrow().getId();
        Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode).orElseThrow();
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode).orElseThrow();

        double convertedAmount;
        double rate = exchangeRateDao.getRate(
                baseCurrency.getId(),
                targetCurrency.getId(),
                usdId);
        if (rate != 0) {
            convertedAmount = (double) amount * rate;
        } else {
            throw new RuntimeException("Exchange rate was not found");
        }

        String formattedConvertAmount = String.format("%.2f", convertedAmount);

        return new ExchangeDto(
                baseCurrency,
                targetCurrency,
                rate,
                (double) amount,
                formattedConvertAmount
        );
    }
}
