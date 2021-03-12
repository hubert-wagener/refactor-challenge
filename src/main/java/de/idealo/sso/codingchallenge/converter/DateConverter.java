package de.idealo.sso.codingchallenge.converter;

import java.util.Calendar;

public interface DateConverter {
    Calendar getCalendarFromString(String date);

    String defaultFormat(Calendar calendar);
}
