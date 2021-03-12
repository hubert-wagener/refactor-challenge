package de.idealo.sso.codingchallenge.restclient;

import de.idealo.sso.codingchallenge.common.CurrencyEnum;

import java.math.BigDecimal;
import java.util.Calendar;


public interface RestClient {
    BigDecimal getCurrentExchangeRates(CurrencyEnum currecnyFrom, CurrencyEnum currecnyTo);
    BigDecimal getHistoricalExchangeRates(CurrencyEnum currencyFrom, CurrencyEnum currencyTo, Calendar calendar);
}
