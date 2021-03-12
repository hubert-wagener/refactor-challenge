package de.idealo.sso.codingchallenge.mvc.controller;

import de.idealo.sso.codingchallenge.common.CurrencyEnum;
import de.idealo.sso.codingchallenge.converter.CurrencyConverter;
import de.idealo.sso.codingchallenge.converter.DateConverter;
import de.idealo.sso.codingchallenge.mvc.model.ConverterFormModel;
import de.idealo.sso.codingchallenge.mvc.model.CountryService;
import de.idealo.sso.codingchallenge.persistence.DefaultProperty;
import de.idealo.sso.codingchallenge.persistence.ErrorEntity;
import de.idealo.sso.codingchallenge.persistence.ErrorRepository;
import de.idealo.sso.codingchallenge.persistence.HistoryEntity;
import de.idealo.sso.codingchallenge.persistence.HistoryRepository;
import de.idealo.sso.codingchallenge.persistence.PropertyService;
import de.idealo.sso.codingchallenge.persistence.UserEntity;
import de.idealo.sso.codingchallenge.persistence.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


import java.math.BigDecimal;
import java.util.Date;
import javax.inject.Inject;

import static de.idealo.sso.codingchallenge.mvc.Consts.*;


@Controller
public class MainWebController {

    private static final String CURRENCY_ENUM = "currencyEnum";
    private static final String RESULT = "result";

    @Inject
    CurrencyConverter currencyConvector;
    @Inject
    DateConverter dateConverter;
    @Inject
    HistoryRepository historyRepository;
    @Inject
    UserRepository userRepository;
    @Inject
    CountryService countryService;
    @Inject
    ErrorRepository errorRepository;
    @Inject
    PropertyService propertyService;
    @Inject
    PasswordEncoder passwordEncoder;

    @PostMapping(CONVERTER_URL)
    public ModelAndView handleConverterForm(ConverterFormModel converterFormModel, Model model, BindingResult bindingResult) {
        try {
            model.addAttribute(CURRENCY_ENUM, CurrencyEnum.values());
            if (converterFormModel.getAmount().doubleValue() > 0 && converterFormModel.getType() != null
                    && !bindingResult.hasErrors()) {
                BigDecimal result = getResult(converterFormModel);
                model.addAttribute(RESULT, String.format("%.3f%n", result));
                saveQuery(converterFormModel, result);
                converterFormModel.setType("current");
                converterFormModel.setDate("");
            } else {
                DefaultProperty property = propertyService.getDefaultProperties();
                converterFormModel.setAmount(property.getDefaultAmount());
                converterFormModel.setTo(property.getDefaultCurrencyTo());
                converterFormModel.setFrom(property.getDefaultCurrencyFrom());
                model.addAttribute(RESULT, "");
            }
            model.addAttribute("history", historyRepository.findFirst10ByOrderByDateCreateDesc());
            return new ModelAndView(CONVERTER_URL);
        } catch (Exception exp) {
            model.addAttribute(RESULT, "");
            model.addAttribute("error", exp.getMessage());
            return new ModelAndView(CONVERTER_URL);
        }
    }

    @RequestMapping(REGISTER_URL)
    public String handleRegisterForm(UserEntity userEntity, Model model) {
        try {
            if (userEntity.getUserName() != null && !userEntity.getUserName().isEmpty()) {
                if (userRepository.findByUserName(userEntity.getUserName()) != null) {
                    model.addAttribute("error", "This UserName already used");
                    model.addAttribute("countries", countryService.getCountriesNames());
                    return REGISTER_URL;
                } else {
                    userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
                    userRepository.save(userEntity);
                    return LOGIN_URL;
                }
            } else {
                model.addAttribute("countries", countryService.getCountriesNames());
                return REGISTER_URL;
            }
        } catch (Exception exp) {
            model.addAttribute("error", exp.getMessage());
            return REGISTER_URL;
        }
    }


    @ExceptionHandler(Exception.class)
    public String handleError(Model model, Exception ex) {
        errorRepository.save(new ErrorEntity("Error in MVC service", ex));
        model.addAttribute("error", ex.getMessage());
        return CONVERTER_URL;
    }

    private void saveQuery(ConverterFormModel converterFormModel, BigDecimal result) {
        historyRepository.save(new HistoryEntity(converterFormModel.getAmount(),
                converterFormModel.getCurrencyEnumFrom(), converterFormModel.getCurrencyEnumTo(),
                new Date(), result, converterFormModel.getType(), converterFormModel.getDate()));
    }

    private BigDecimal getResult(ConverterFormModel converterFormModel) {
        return currencyConvector.getConvertingValue(
                converterFormModel.isHistory(),
                converterFormModel.getAmount(),
                converterFormModel.getCurrencyEnumFrom(),
                converterFormModel.getCurrencyEnumTo(),
                dateConverter.getCalendarFromString(converterFormModel.getDate())
        );
    }
}
