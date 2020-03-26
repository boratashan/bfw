package com.mybaas.utils;

import java.util.UUID;

public class UIDUtils {

    public static String getUniqueID() {
        return UUID.randomUUID().toString();
    }
}
