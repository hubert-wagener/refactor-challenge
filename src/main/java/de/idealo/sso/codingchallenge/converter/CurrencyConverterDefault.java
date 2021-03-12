package de.idealo.sso.codingchallenge.converter;

import de.idealo.sso.codingchallenge.common.CurrencyEnum;
import de.idealo.sso.codingchallenge.exceptions.ConverterException;
import de.idealo.sso.codingchallenge.restclient.RestClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Calendar;

@Service
public class CurrencyConverterDefault implements CurrencyConverter {
    @Inject
    @Qualifier("FaultTolerant")
    RestClient restClient;


    private BigDecimal getConvertingCurrentValue(BigDecimal amount, CurrencyEnum currencyFrom, CurrencyEnum currencyTo) {
        return amount.multiply(restClient.getCurrentExchangeRates(currencyFrom, currencyTo));
    }

    private BigDecimal getConvertingHistoricalValue(BigDecimal amount, CurrencyEnum currencyFrom, CurrencyEnum currencyTo, Calendar calendar) {
        return amount.multiply(restClient.getHistoricalExchangeRates(currencyFrom, currencyTo, calendar));
    }

    @Override
    public BigDecimal getConvertingValue(Boolean isHistory, BigDecimal amount, CurrencyEnum currencyFrom,
                                         CurrencyEnum currencyTo, Calendar calendar) {
        if(isHistory) {
            if(calendar != null) {
                return getConvertingHistoricalValue(amount, currencyFrom, currencyTo, calendar);
            } else {
                throw new ConverterException("'Date' must be filled in historical convection");
            }
        } else {
            return getConvertingCurrentValue(amount, currencyFrom, currencyTo);
        }
    }

}
