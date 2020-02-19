package b.utils;

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
}
