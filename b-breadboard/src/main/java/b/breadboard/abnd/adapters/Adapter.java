package b.breadboard.abnd.adapters;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface Adapter {
     File read(InputStream inputStream) throws IOException;
}
