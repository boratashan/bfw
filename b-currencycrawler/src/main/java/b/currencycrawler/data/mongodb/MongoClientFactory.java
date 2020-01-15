package b.currencycrawler.data.mongodb;


import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class MongoClientFactory {

    private static MongoClientFactory instance;
    private  JsonObject configuration;



    private static synchronized MongoClientFactory getInstance() throws MongoClientConfigurationException {
        if (instance==null){
            instance = new MongoClientFactory();
            instance.configuration = instance.loadConfiguration();
        }
        return instance;
    }


    private JsonObject loadConfiguration() throws MongoClientConfigurationException {
        MongoClientConfigurationLoader configurationLoader = new MongoClientConfigurationLoader();
        return configurationLoader.loadConfiguration();
    }


    private MongoClient createNonShared(Vertx vertx) {
        return MongoClient.createNonShared(vertx, configuration);
    }





    public static  MongoClient buildClient(Vertx vertx) throws MongoClientConfigurationException {
        return getInstance().createNonShared(vertx);
    }




}
