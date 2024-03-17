package ru.alshevskiy.currencyExchange.dto;

import ru.alshevskiy.currencyExchange.entity.Currency;

public record ExchangeDto(Currency baseCurrency,
                          Currency targetCurrency,
                          double rate,
                          double amount,
                          String convertedAmount) {
}
