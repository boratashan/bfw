package com.mybaas.commons.mongodb;

import com.mybaas.utils.ResourceUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.io.IOException;

public class MongoClientConfigurationLoader {

    public JsonObject loadConfiguration() throws MongoClientConfigurationException {
        try {
            String configuration = ResourceUtils.getResourceAsString(MongoClientConstants.DEFAULT_CONFIGURATION_JSON_FILE_NAME);
            if (configuration==null){
                throw new MongoClientConfigurationException(String.format("Can not find mongodb configuration file %s", MongoClientConstants.DEFAULT_CONFIGURATION_JSON_FILE_NAME));
            }
            JsonObject json = new JsonObject(configuration);
            return json;
        } catch (IOException e) {
            throw new MongoClientConfigurationException("", e);
        }
    }
}
