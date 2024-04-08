# Currency Exchange REST API

Проект выполнен в соответствии с техническим заданием [курса](https://zhukovsd.github.io/java-backend-learning-course/Projects/CurrencyExchange/).

## Описание

REST API для описания валют и обменных курсов. Позволяет просматривать и редактировать списки валют и обменных курсов, 
и совершать расчёт конвертации произвольных сумм из одной валюты в другую.

Веб-интерфейс для проекта не подразумевается.

## Технологии

- JDBC
- Java Servlets
- SQLite
- Postman
- Maven
- Jackson

## Диаграмма базы данных

![image](https://github.com/alshevskiy/currency-exchange/assets/111909057/7ba3726a-4510-420b-aed1-7149ff72e998)

## API

### Currencies

#### GET `/currencies`

Получение списка валют. 
>
Пример ответа:

```json
[
  {
    "id": 0,
    "name": "United States dollar",
    "code": "USD",
    "sign": "$"
  },
  {
    "id": 1,
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
  },
  "..."
]
```

#### GET `/currency/USD`

Получение конкретной валюты. 
>
Пример ответа:

```json
[
  {
    "id": 0,
    "name": "United States dollar",
    "code": "USD",
    "sign": "$"
  }
]
```

#### POST `/currencies`

Добавление новой валюты в базу. Данные передаются в теле запроса в виде полей формы (x-www-form-urlencoded). 
Поля формы - `name`, `code`, `sign`. 
>
Пример ответа - JSON представление вставленной в базу записи, включая её ID:

```json
[
  {
    "id": 1,
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
  }
]
```

### Exchange rates

#### GET `/exchangeRates`

Получение списка всех обменных курсов. Пример ответа:

```json
[
  {
    "id": 0,
    "baseCurrency": {
      "id": 0,
      "name": "United States dollar",
      "code": "USD",
      "sign": "$"
    },
    "targetCurrency": {
      "id": 1,
      "name": "Euro",
      "code": "EUR",
      "sign": "€"
    },
    "rate": 0.93
  }
]
```

#### GET `/exchangeRates/USDEUR`

Получение конкретного обменного курса. Валютная пара задаётся идущими подряд кодами валют в адресе запроса. 
>
Пример ответа:

```json
[
  {
    "id": 0,
    "baseCurrency": {
      "id": 0,
      "name": "United States dollar",
      "code": "USD",
      "sign": "$"
    },
    "targetCurrency": {
      "id": 1,
      "name": "Euro",
      "code": "EUR",
      "sign": "€"
    },
    "rate": 0.93
  }
]
```

#### POST `/exchangeRates`
Добавление нового обменного курса в базу. Данные передаются в теле запроса в виде полей формы (`x-www-form-urlencoded`).
Поля формы - `baseCurrencyCode`, `targetCurrencyCode`, `rate`. 
>
Пример ответа - JSON представление вставленной в базу записи, включая её ID:

```json
[
  {
    "id": 0,
    "baseCurrency": {
      "id": 0,
      "name": "United States dollar",
      "code": "USD",
      "sign": "$"
    },
    "targetCurrency": {
      "id": 1,
      "name": "Euro",
      "code": "EUR",
      "sign": "€"
    },
    "rate": 0.93
  }
]
```

#### PATCH `/exchangeRate/USDEUR`

Обновление существующего в базе обменного курса. Валютная пара задаётся идущими подряд кодами валют в адресе запроса. 
Данные передаются в теле запроса в виде полей формы (`x-www-form-urlencoded`). Единственное поле формы - `rate`.
>
Пример ответа - JSON представление обновлённой записи в базе данных, включая её ID:

```json
[
  {
    "id": 1,
    "baseCurrency": {
      "id": 0,
      "name": "United States dollar",
      "code": "USD",
      "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Euro",
        "code": "EUR",
        "sign": "€"
    },
    "rate": 0.99
  }
]
```

## Currency exchange

#### GET `/exchange?from=BASE_CURRENCY_CODE&to=TARGET_CURRENCY_CODE&amount=$AMOUNT`

Расчёт перевода определённого количества средств из одной валюты в другую. Пример запроса - GET `/exchange?from=USD&to=AUD&amount=10`
>
Пример ответа:

```json
{
  "baseCurrency": {
    "id": 0,
    "name": "United States dollar",
    "code": "USD",
    "sign": "$"
  },
  "targetCurrency": {
        "id": 1,
        "name": "Australian dollar",
        "code": "AUD",
        "sign": "A€"
  },
  "rate": 1.45,
  "amount": 10.00,
  "convertedAmount": 14.50
}
```
