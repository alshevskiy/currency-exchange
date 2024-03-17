package ru.alshevskiy.currencyExchange.service;

import ru.alshevskiy.currencyExchange.dao.ExchangeRateDao;
import ru.alshevskiy.currencyExchange.dto.ExchangeRateDto;
import ru.alshevskiy.currencyExchange.entity.ExchangeRate;

import java.util.List;

public class ExchangeRateService {
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();

    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();

    private ExchangeRateService() {
    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }

    public ExchangeRateDto findById(Long id) {
        return buildDto(
                exchangeRateDao.findById(id).orElseThrow()
        );
    }

    public List<ExchangeRateDto> findAll() {
        return exchangeRateDao.findAll()
                .stream()
                .map(this::buildDto)
                .toList();
    }

    public ExchangeRateDto findByBaseAndTargetCurrencyCode(String baseCurrencyCode, String targetCurrencyCode) {
        return buildDto(
                exchangeRateDao.findByBaseAndTargetCurrencyId(
                        currencyService.findByCode(baseCurrencyCode).id(),
                        currencyService.findByCode(targetCurrencyCode).id()
                ).orElseThrow()
        );
    }

    public ExchangeRateDto save(String baseCurrencyCode, String targetCurrencyCode, Double rate) {
        ExchangeRate savedExchangeRate = exchangeRateDao.save(
                new ExchangeRate(
                        null,
                        currencyService.findByCode(baseCurrencyCode).id(),
                        currencyService.findByCode(targetCurrencyCode).id(),
                        rate
                )
        );
        return buildDto(savedExchangeRate);
    }

    public ExchangeRateDto updateByCurrencyPair(String currencyPair, Double rate) {
        String baseCurrencyCode = currencyPair.substring(0, 3);
        String targetCurrencyCode = currencyPair.substring(3);

        Long baseCurrencyId = currencyService.findByCode(baseCurrencyCode).id();
        Long targetCurrencyId = currencyService.findByCode(targetCurrencyCode).id();

        ExchangeRate updatedExchangeRate = exchangeRateDao.updateByCurrencyPair(
                new ExchangeRate(
                        null,
                        baseCurrencyId,
                        targetCurrencyId,
                        rate
                )
        );
        return buildDto(updatedExchangeRate);
    }

    private ExchangeRateDto buildDto(ExchangeRate exchangeRate) {
        return new ExchangeRateDto(
                exchangeRate.getId(),
                currencyService.findById(exchangeRate.getBaseCurrencyId()),
                currencyService.findById(exchangeRate.getTargetCurrencyId()),
                exchangeRate.getRate()
        );
    }

}
