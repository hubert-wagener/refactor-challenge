package de.idealo.sso.codingchallenge;

import de.idealo.sso.codingchallenge.mvc.model.CountryService;
import de.idealo.sso.codingchallenge.mvc.model.CountryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CountryServiceTest {
    @Test
    public void testService() {
        CountryService countryService = new CountryServiceImpl();
        List<String> lists = countryService.getCountriesNames();
        Assertions.assertNotNull(lists);
        Assertions.assertTrue(lists.size() > 0);
     }
}
