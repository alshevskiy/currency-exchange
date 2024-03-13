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

    private ExchangeRateDto buildDto(ExchangeRate exchangeRate) {
        return new ExchangeRateDto(
                exchangeRate.getId(),
                currencyService.findById(exchangeRate.getBaseCurrencyId()),
                currencyService.findById(exchangeRate.getTargetCurrencyId()),
                exchangeRate.getRate()
        );
    }
}
