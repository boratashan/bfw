package com.mybaas.utils;

import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.stream.Collectors;

public class ResourceUtils {

    public static String getResourceAsString(String resourceName) throws IOException {
        ClassLoader classLoader = ResourceUtils.class.getClassLoader();
        /*if (Objects.isNull(classLoader.getResource(resourceName))) {
            classLoader = ClassLoader.getSystemClassLoader();
        }*/

        try (InputStream is = classLoader.getResourceAsStream(resourceName)) {
            if (is == null) return null;
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }


}
