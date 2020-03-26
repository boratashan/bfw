package com.mybaas.commons.mysql;

import com.mybaas.utils.ResourceUtils;
import io.vertx.core.json.JsonObject;

import java.io.IOException;

public class MySqlClientConfigurationLoader {

    public JsonObject loadConfiguration() throws MySqlClientConfigurationException {
        try {
            String configuration = ResourceUtils.getResourceAsString(MySqlClientConstants.DEFAULT_CONFIGURATION_JSON_FILE_NAME);
            if (configuration==null){
                throw new MySqlClientConfigurationException(String.format("Can not find mongodb configuration file %s", MySqlClientConstants.DEFAULT_CONFIGURATION_JSON_FILE_NAME));
            }
            JsonObject json = new JsonObject(configuration);
            return json;
        } catch (IOException e) {
            throw new MySqlClientConfigurationException("", e);
        }
    }
}
