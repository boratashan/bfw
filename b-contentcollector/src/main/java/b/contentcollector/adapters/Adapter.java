package b.contentcollector.adapters;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface Adapter {
     void read(InputStream inputStream) throws IOException;
}
