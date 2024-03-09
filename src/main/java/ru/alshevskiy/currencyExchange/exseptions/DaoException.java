package ru.alshevskiy.currencyExchange.exseptions;

public class DaoException extends RuntimeException {
    public DaoException(Throwable e) {
        super(e);
    }
}
