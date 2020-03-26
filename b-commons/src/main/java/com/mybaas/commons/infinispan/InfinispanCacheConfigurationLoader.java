package com.mybaas.commons.infinispan;

import com.mybaas.utils.ResourceUtils;
import io.vertx.core.json.JsonObject;

import java.io.IOException;

public class InfinispanCacheConfigurationLoader {

    public JsonObject loadConfiguration() throws InfinispanCacheConfigurationException {
        try {
            String configuration = ResourceUtils.getResourceAsString(InfinispanCacheConstants.DEFAULT_CONFIGURATION_JSON_FILE_NAME);
            if (configuration==null){
                throw new InfinispanCacheConfigurationException(String.format("Can not find infinispan configuration file %s", InfinispanCacheConstants.DEFAULT_CONFIGURATION_JSON_FILE_NAME));
            }
            JsonObject json = new JsonObject(configuration);
            return json;

        } catch (IOException e) {
            throw new InfinispanCacheConfigurationException("", e);
        }
    }
}
