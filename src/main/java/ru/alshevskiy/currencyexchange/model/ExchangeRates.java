package main.java.ru.alshevskiy.currencyexchange.model;

public class ExchangeRates {
    int id;
    double rate; // Курс обмена единицы базовой валюты к единице целевой валюты

    public ExchangeRates(int id, double rate) {
        this.id = id;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
