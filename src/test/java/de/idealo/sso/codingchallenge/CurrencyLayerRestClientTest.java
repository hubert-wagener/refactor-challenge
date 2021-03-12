package de.idealo.sso.codingchallenge;

import de.idealo.sso.codingchallenge.common.CurrencyEnum;
import de.idealo.sso.codingchallenge.converter.DateConverterDefault;
import de.idealo.sso.codingchallenge.exceptions.RestClientException;
import de.idealo.sso.codingchallenge.restclient.RestClient;
import de.idealo.sso.codingchallenge.restclient.impl.currencylayer.CurrencyLayerRestClient;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.GregorianCalendar;


public class CurrencyLayerRestClientTest {
    @Test
    public void ExternalTest() {
        RestClient restClient = new CurrencyLayerRestClient(new DateConverterDefault());
        BigDecimal convertRates = restClient.getCurrentExchangeRates(CurrencyEnum.EUR, CurrencyEnum.RUB);
        Assertions.assertTrue(convertRates.doubleValue() > 0.0);
        convertRates = restClient.getHistoricalExchangeRates(CurrencyEnum.EUR, CurrencyEnum.RUB, new GregorianCalendar(2010, 10, 17));
        Assertions.assertEquals(convertRates.doubleValue(), 42.6, 0.5);
        convertRates = restClient.getHistoricalExchangeRates(CurrencyEnum.EUR, CurrencyEnum.RUB, new GregorianCalendar(2012, 1, 1));
        Assertions.assertEquals(convertRates.doubleValue(), 39.7, 0.5);
    }

    @Test
    public void ExternalErrorTest() {
        RestClient restClient = new CurrencyLayerRestClient(new DateConverterDefault());
        try {
            BigDecimal convertRates = restClient.getHistoricalExchangeRates(CurrencyEnum.EUR, CurrencyEnum.RUB, new GregorianCalendar(2021, 12, 47));
            Assertions.fail("Must throw RestClientException");
        } catch ( RestClientException exception) {
            Assertions.assertEquals("Error while gathering information from CurrencyLayer services, code:302, error:"
                    + "You have entered an invalid date. [Required format: date=YYYY-MM-DD]",exception.getMessage());
        }
    }

}
