package com.mybaas.commons.mongodb;

import com.mybaas.commons.config.ApplicationConfigManager;
import com.mybaas.commons.exceptions.ApplicationInitializationException;
import com.mybaas.utils.ResourceUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.io.IOException;

public class MongoClientConfigurationLoader {

    public JsonObject loadConfiguration() throws MongoClientConfigurationException {
        try {
            return ApplicationConfigManager.get().getPlatformConfig().getJsonObject("mongodb");
            /*
            String configuration = ResourceUtils.getResourceAsString(MongoClientConstants.DEFAULT_CONFIGURATION_JSON_FILE_NAME);
            if (configuration==null){
                throw new MongoClientConfigurationException(String.format("Can not find mongodb configuration file %s", MongoClientConstants.DEFAULT_CONFIGURATION_JSON_FILE_NAME));
            }
            JsonObject json = new JsonObject(configuration);
            return json;
             */
        } catch (ApplicationInitializationException e) {
            throw new MongoClientConfigurationException("", e);
        }
    }
}
