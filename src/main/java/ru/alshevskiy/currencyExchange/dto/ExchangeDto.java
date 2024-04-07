package ru.alshevskiy.currencyExchange.dto;

import ru.alshevskiy.currencyExchange.model.Currency;

public record ExchangeDto(Currency baseCurrency,
                          Currency targetCurrency,
                          double rate,
                          double amount,
                          String convertedAmount) {
}
