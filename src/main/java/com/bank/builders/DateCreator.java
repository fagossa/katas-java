package com.bank.builders;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateCreator {

    public static Date date(String string) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        try {
            return format.parse(string);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date");
        }
    }
}
