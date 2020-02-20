package b.contentcollector.targetsuppliers;

import java.net.MalformedURLException;
import java.net.URL;

public class Target {
    private final URL url;
    private final String id;
    
    public URL getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public Target(String id, String TargetUrl) throws MalformedURLException {
        this.id = id;
        this.url = new URL(TargetUrl);
    }
}
