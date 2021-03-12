package de.idealo.sso.codingchallenge;

import de.idealo.sso.codingchallenge.common.CurrencyEnum;
import de.idealo.sso.codingchallenge.exceptions.RestClientException;
import de.idealo.sso.codingchallenge.restclient.RestClient;
import de.idealo.sso.codingchallenge.restclient.impl.openexchange.OpenExchangeRestClient;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.GregorianCalendar;

public class OpenExchangeRestClientTest {
    @Test
    public void ExternalTest() {
        RestClient restClient = new OpenExchangeRestClient();
        BigDecimal convertRates = restClient.getCurrentExchangeRates(CurrencyEnum.EUR, CurrencyEnum.RUB);
        Assertions.assertThat(convertRates.doubleValue()).isGreaterThan(0.0);
        convertRates = restClient.getHistoricalExchangeRates(CurrencyEnum.EUR, CurrencyEnum.RUB,
                new GregorianCalendar(2010, 10, 17));
        Assertions.assertThat(convertRates.doubleValue()).isEqualTo(42.6, Offset.offset(0.5));
        convertRates = restClient.getHistoricalExchangeRates(CurrencyEnum.EUR, CurrencyEnum.RUB,
                new GregorianCalendar(2012, 1, 1));
        Assertions.assertThat(convertRates.doubleValue()).isEqualTo(39.7, Offset.offset( 0.5));
    }

    @Test
    public void ExternalErrorTest() {
        RestClient restClient = new OpenExchangeRestClient();
        try {
            BigDecimal convertRates = restClient.getHistoricalExchangeRates(CurrencyEnum.EUR, CurrencyEnum.RUB, new GregorianCalendar(2110, 12, 47));
            Assertions.fail("Must be throw RestClientException");
        } catch ( RestClientException exception) {
            Assertions.assertThat(exception.getMessage())
                    .startsWith("Error while gathering information from OpenExchange services, code:400, error:true");
        }
    }

}
