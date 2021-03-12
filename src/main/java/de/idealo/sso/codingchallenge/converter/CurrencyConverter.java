package de.idealo.sso.codingchallenge.converter;

import de.idealo.sso.codingchallenge.common.CurrencyEnum;

import java.math.BigDecimal;
import java.util.Calendar;


public interface CurrencyConverter {
    BigDecimal getConvertingValue(Boolean isHistory, BigDecimal amount, CurrencyEnum currencyFrom,
                                  CurrencyEnum currencyTo, Calendar calendar);
}
