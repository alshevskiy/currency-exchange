package ru.alshevskiy.currencyExchange.dto;

public record ExchangeRateDto(Long id,
                              Long baseCurrencyId,
                              Long targetCurrencyId,
                              Double rate) {
}
