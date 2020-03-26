package com.mybaas.utils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class DateTimeUtils {


    private static final String FORMAT_SIMPLE_DATE_TIME = "YYYYMMDDHHmmSSNNN";
    private static final String FORMAT_UTC_DATE_TIME = "YYYY-MM-dd'T'HH:mm:ss.SSSXXX";

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static String toString(LocalDateTime dateTime, DateTimeFormat format) {
        String result;
        switch (format) {
            case SIMPLEDATE:
            case SIMPLETIME:
                throw new UnsupportedOperationException();
            case SIMPLEDATETIME:
                result = dateTime.format(DateTimeFormatter.ofPattern(FORMAT_SIMPLE_DATE_TIME));
                break;
            case UTCDATETIME:
                result = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(FORMAT_UTC_DATE_TIME));
                //result = dateTime.format(DateTimeFormatter.ofPattern(FORMAT_UTC_DATE_TIME).withZone(ZoneId.of("UTC")));
                break;
            case ISODATETIME:
                result = dateTime.format(DateTimeFormatter.ISO_INSTANT);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + format);
        }
        return result;
    }

    public static LocalDateTime fromString(String value, DateTimeFormat format)  {
        switch (format) {
            case UTCDATETIME:
                return LocalDateTime.parse(value, DateTimeFormatter.ISO_ZONED_DATE_TIME);
            case SIMPLEDATE:
            case SIMPLEDATETIME:
            case SIMPLETIME:
            case ISODATETIME:
            default:
                throw new IllegalStateException("Unexpected value: " + format);
        }

    }

    public static String nowString(DateTimeFormat format) {
        return  toString(LocalDateTime.now(), format);
    }



}
