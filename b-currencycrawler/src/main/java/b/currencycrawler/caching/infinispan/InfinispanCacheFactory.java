package b.currencycrawler.caching.infinispan;


import io.vertx.core.json.JsonObject;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

public class InfinispanCacheFactory {

    private static InfinispanCacheFactory instance;

    private RemoteCacheManager cacheManager;
    private JsonObject configuration;


    private static synchronized InfinispanCacheFactory getInstance() throws InfinispanCacheConfigurationException {
        if (instance==null){
            instance = new InfinispanCacheFactory();
            getInstance().configuration = instance.loadConfiguration();
            getInstance().setup();
        }
        return instance;
    }


    private JsonObject loadConfiguration() throws InfinispanCacheConfigurationException {
        return new InfinispanCacheConfigurationLoader().loadConfiguration();
    }


    private void setup() {
        Configuration configuration = new ConfigurationBuilder()
                .addServer()
                    .host(this.configuration.getString("server"))
                    .port(this.configuration.getInteger("port"))
                .clientIntelligence(ClientIntelligence.BASIC)
                .build();
        this.cacheManager = new RemoteCacheManager(configuration);
    }



    public static  RemoteCacheManager getCacheManager() throws InfinispanCacheConfigurationException {
        return getInstance().cacheManager;

    }




}
