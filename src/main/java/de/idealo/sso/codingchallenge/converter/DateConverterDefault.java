package de.idealo.sso.codingchallenge.converter;

import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


@Service
public class DateConverterDefault implements DateConverter {

    @Override
    public Calendar getCalendarFromString(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        } else {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date date;
            try {
                date = df.parse(dateString);
            } catch ( ParseException e ) {
                throw new RuntimeException();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        }
    }

    @Override
    public String defaultFormat(Calendar calendar) {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(calendar.getTime());
    }
}
