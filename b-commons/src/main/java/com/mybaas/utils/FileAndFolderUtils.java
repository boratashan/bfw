package com.mybaas.utils;

import java.io.*;

public  class FileAndFolderUtils {
    public static final char FILE_EXTENSION_SEPERATOR = '.';

    public static String includeFileExtension(String fileName, String ext) {
        String result;
        if (!ext.startsWith(String.valueOf(FILE_EXTENSION_SEPERATOR))) {
                result = fileName.concat(".").concat(ext);
        }
        else {
            result = fileName.concat(ext);
        }
        return result;
    }



    public static String readFileAsString(File file) throws IOException {
        try (FileReader fr = new FileReader(file)) {
            BufferedReader reader = new BufferedReader(fr);
            StringBuilder sb = new StringBuilder();
            String s;
            while ((s = reader.readLine()) != null) {
                sb.append(s);
            }
            return sb.toString();
        }
    }
}
