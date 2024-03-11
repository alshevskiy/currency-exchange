package ru.alshevskiy.currencyExchange.exseption;

public class DaoException extends RuntimeException {
    public DaoException(Throwable e) {
        super(e);
    }
}
