package com.mybaas.utils;

import java.util.Base64;

public class StringUtils {

    public static String encodeByBase64(String input){
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    public static String decodeByBase64(String input) {
        return  new String(Base64.getDecoder().decode(input));

    }
}
