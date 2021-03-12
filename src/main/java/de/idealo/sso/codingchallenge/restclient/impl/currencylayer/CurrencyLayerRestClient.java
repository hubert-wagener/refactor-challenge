package de.idealo.sso.codingchallenge.restclient.impl.currencylayer;

import de.idealo.sso.codingchallenge.converter.DateConverter;
import de.idealo.sso.codingchallenge.exceptions.RestClientException;
import de.idealo.sso.codingchallenge.restclient.RestClient;
import de.idealo.sso.codingchallenge.common.CurrencyEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Calendar;

import static java.math.BigDecimal.ROUND_FLOOR;

@Service("CurrencyLayer")
@RequiredArgsConstructor
public class CurrencyLayerRestClient implements RestClient {
    private final static String URL_LAST = "http://www.apilayer.net/api/live?format=1";
    private final static String URL_HISTORY = "http://apilayer.net/api/historical?date=";
    private final static String API_ID = "f7e5948888d41713110273b47c682db0";
    private final static String API_ID_PRM = "&access_key=";
    private static final String ERROR_WHILE_GATHERING_INFORMATION = "Error while gathering information from CurrencyLayer services";

    private final static String URL = "http://www.apilayer.net/api/";
    private final static String LIVE_PATH = "live";
    private final static String LIVE_PARAM_FORMAT = "format";
    private final static String HISTORY_PATH = "historical";
    private final static String HISTORY_PARAM_DATE = "date";
    private final static String API_KEY_PARAM = "access_key";


    private final DateConverter dateConverter; //

    private UriComponentsBuilder currencyServiceUriBuilder(){
        return  UriComponentsBuilder.fromUriString(URL).queryParam(API_KEY_PARAM, API_ID);
    }

    public BigDecimal getCurrentExchangeRates(CurrencyEnum currencyFrom, CurrencyEnum currencyTo) {
        return getExchangeRates(currencyFrom, currencyTo, getCurrentRates());
    }

    public BigDecimal getHistoricalExchangeRates(CurrencyEnum currencyFrom, CurrencyEnum currencyTo, Calendar calendar) {
        return getExchangeRates(currencyFrom, currencyTo, getHistoricalRates(calendar));
    }

    private BigDecimal getExchangeRates(CurrencyEnum currencyFrom, CurrencyEnum currencyTo, CurrencyLayerRatesContainer rates) {
        BigDecimal currencyFromRate = getRates(currencyFrom, rates);
        BigDecimal currencyToRate = getRates(currencyTo, rates);
        return currencyToRate.divide(currencyFromRate, 20, ROUND_FLOOR);
    }

    public CurrencyLayerRatesContainer getCurrentRates() {
        final UriComponents uri = currencyServiceUriBuilder()
                .path(LIVE_PATH)
                .queryParam(LIVE_PARAM_FORMAT, "1")
                .build();
        return getEntity(uri.toUriString(), CurrencyLayerRatesContainer.class);
    }

    public CurrencyLayerRatesContainer getHistoricalRates(Calendar date) {
        String dateString = dateConverter.defaultFormat(date);
        final UriComponents uri = currencyServiceUriBuilder()
                .path(HISTORY_PATH)
                .queryParam(HISTORY_PARAM_DATE, dateString).build();
        return getEntity(uri.toUriString(), CurrencyLayerRatesContainer.class);
    }

    private String getStringWithLeftPadZero(int number) {
        return String.format("%02d", number);
    }

    private static BigDecimal getRates(CurrencyEnum currency, CurrencyLayerRatesContainer rates) {
        if (rates.getQuotes() == null) {
            throw new RestClientException(ERROR_WHILE_GATHERING_INFORMATION +
                    (rates.getError() != null ? ", code:" + rates.getError().getCode() + ", error:" +
                            rates.getError().getInfo() : ""));
        } else {
            String rate = rates.getQuotes().get("USD" + currency.getCode());
            return new BigDecimal(rate);
        }
    }

    private static <T> T getEntity(String url, Class<T> entityClass) {
        Response response = null;
        try {
            Client client = ClientBuilder.newBuilder().build();
            response = client.target(url).request().get();
            return response.readEntity(entityClass);
        } catch (Exception exp) {
            throw new RestClientException(ERROR_WHILE_GATHERING_INFORMATION + ", error: " +
                    (response != null? response.getStatusInfo(): exp.getMessage()));
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }


}
