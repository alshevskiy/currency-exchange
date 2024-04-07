package ru.alshevskiy.currencyExchange.dto;

import ru.alshevskiy.currencyExchange.model.Currency;

import java.math.BigDecimal;

public record ExchangeRateDto(Long id,
                              Currency baseCurrency,
                              Currency targetCurrency,
                              BigDecimal rate) {
}
