package b.currencycrawler.caching.infinispan;

public class InfinispanCacheConfigurationException extends Exception {
    public InfinispanCacheConfigurationException(String message) {
        super(message);
    }

    public InfinispanCacheConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
