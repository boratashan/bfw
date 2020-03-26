package com.mybaas.commons.amqpclient;


import io.vertx.amqp.AmqpClient;
import io.vertx.amqp.AmqpClientOptions;
import io.vertx.core.json.JsonObject;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

public class AmqpClientFactory {

    private static AmqpClientFactory instance;

    private AmqpClient client;
    private JsonObject configuration;


    private static synchronized AmqpClientFactory getInstance() throws AmqpClientConfigurationException {
        if (instance==null){
            instance = new AmqpClientFactory();
            getInstance().configuration = instance.loadConfiguration();
            getInstance().setup();
        }
        return instance;
    }


    private JsonObject loadConfiguration() throws AmqpClientConfigurationException {
        return new AmqpClientConfigurationLoader().loadConfiguration();
    }


    private void setup() {
        AmqpClientOptions options = new AmqpClientOptions()
                .setHost(this.configuration.getString("server"))
                .setPort(this.configuration.getInteger("port"))
                .setUsername("admin")
                .setPassword("admin");
        this.client = AmqpClient.create(options);
    }

    public static  AmqpClient getAmqpClient() throws AmqpClientConfigurationException {
        return getInstance().client;

    }




}
