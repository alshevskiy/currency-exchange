package ru.alshevskiy.currencyExchange.exception;

public class DaoException extends RuntimeException {
    public DaoException(Throwable e) {
        super(e);
    }
}
