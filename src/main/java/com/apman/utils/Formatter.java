package com.apman.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Formatter {
    private static DateTimeFormatter _dateTimeformatter = DateTimeFormatter.ofPattern("MMMM d, uuuu - hh:mm a", Locale.ENGLISH);
    private static DateTimeFormatter _dateFormatter = DateTimeFormatter.ofPattern("MMMM d, uuuu", Locale.ENGLISH);

    public static String formatDateTime (LocalDateTime datetime) {
        return datetime.format(_dateTimeformatter);
    }

    public static String formatDate (LocalDate date) {
        return date.format(_dateFormatter);
    }
}
