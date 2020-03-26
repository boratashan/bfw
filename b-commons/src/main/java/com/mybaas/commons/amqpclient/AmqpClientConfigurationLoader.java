package com.mybaas.commons.amqpclient;

import com.mybaas.utils.ResourceUtils;
import io.vertx.core.json.JsonObject;

import java.io.IOException;

public class AmqpClientConfigurationLoader {

    public JsonObject loadConfiguration() throws AmqpClientConfigurationException {
        try {
            String configuration = ResourceUtils.getResourceAsString(AmqpClientConstants.DEFAULT_CONFIGURATION_JSON_FILE_NAME);
            if (configuration==null){
                throw new AmqpClientConfigurationException(String.format("Can not find amqp client configuration file %s", AmqpClientConstants.DEFAULT_CONFIGURATION_JSON_FILE_NAME));
            }
            JsonObject json = new JsonObject(configuration);
            return json;
        } catch (IOException e) {
            throw new AmqpClientConfigurationException("", e);
        }
    }
}
