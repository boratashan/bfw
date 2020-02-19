package b.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Http {

    public static void fetchContent(URL url) throws IOException {
        InputStream inputStream =  url.openStream();
        try {
            BufferedInputStream input = new BufferedInputStream(inputStream);
            input.close();
        }
        finally {
            inputStream.close();
        }

    }

}
