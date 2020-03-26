package com.mybaas.utils;

import java.text.DecimalFormatSymbols;

public class NumberUtils {

    public static Double forceParsingFromString(String  value) {
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        char ds = formatSymbols.getDecimalSeparator();
        try {
            return Double.parseDouble(value);
        }
        catch (NumberFormatException e) {
            char c = (ds == '.')?',':'.';
            value = value.replace(c, ds);
            return Double.parseDouble(value);
        }
    }
}
