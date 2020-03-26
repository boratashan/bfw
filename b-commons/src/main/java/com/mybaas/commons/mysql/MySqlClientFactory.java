package com.mybaas.commons.mysql;


import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;

public class MySqlClientFactory {

    private static MySqlClientFactory instance;
    private  JsonObject configuration;



    private static synchronized MySqlClientFactory getInstance() throws MySqlClientConfigurationException {
        if (instance==null){
            instance = new MySqlClientFactory();
            instance.configuration = instance.loadConfiguration();
        }
        return instance;
    }


    private JsonObject loadConfiguration() throws MySqlClientConfigurationException {
        MySqlClientConfigurationLoader configurationLoader = new MySqlClientConfigurationLoader();
        return configurationLoader.loadConfiguration();
    }


    private MySQLPool getClient(Vertx vertx) {
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setPort(configuration.getInteger("port"))
                .setHost(configuration.getString("host"))
                .setDatabase(configuration.getString("database"))
                .setUser(configuration.getString("user"))
                .setPassword(configuration.getString("password"));
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(5);
        MySQLPool client = MySQLPool.pool(vertx, connectOptions, poolOptions);
        return client;
    }





    public static  MySQLPool buildClient(Vertx vertx) throws MySqlClientConfigurationException {
        return getInstance().getClient(vertx);
    }




}
