package ru.alshevskiy.currencyExchange.util;

import ru.alshevskiy.currencyExchange.exception.*;

import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;

public class Validation {
    private static Set<String> currencyCodes;

    public static void doCurrencyParametersValidation(String name, String code, String sign) throws MissingParameterException, NotComplyISO4217Exception {
        doNameValidation(name);
        doCodeValidation(code);
        doSignValidation(sign);
    }

    private static void doNameValidation(String name) throws MissingParameterException {
        if (name == null || name.isBlank()) {
            throw new MissingParameterException("Отсуствует параметр 'name'");
        }
    }

    public static void doCodeValidation (String code) throws MissingParameterException, NotComplyISO4217Exception {
        if (code == null || code.isBlank()) {
            throw new MissingParameterException("Отсуствует параметр 'code'");
        }

        if (currencyCodes == null) {
            Set<Currency> currencies = Currency.getAvailableCurrencies();
            currencyCodes = currencies.stream()
                    .map(Currency::getCurrencyCode)
                    .collect(Collectors.toSet());
        }

        if (!currencyCodes.contains(code)) {
            throw new NotComplyISO4217Exception("Валюта не соответвуетствует стандарту ISO 4217");
        }
    }

    private static void doSignValidation(String sign) throws MissingParameterException {
        if (sign == null || sign.isBlank()) {
            throw new MissingParameterException("Отсуствует параметр 'sign'");
        }
    }

    public static void doLengthValidation(String currencyPair) throws IllegalFormatException {
        if (currencyPair.length() != 6) {
            throw new IllegalFormatException("Валютная пара не указана, либо указана с ошибкой");
        }
    }

    public static void doRateValidation(String body) throws MissingParameterException {
        if (body == null || !body.contains("rate")) {
            throw new MissingParameterException("Отсутсвует параметр 'rate'");
        }
    }

    public static void doAmountValidation(String amount) throws MissingParameterException {
        if (amount == null || amount.isBlank()) {
            throw new MissingParameterException("Отсуствует параметр 'amount'");
        }
    }
}