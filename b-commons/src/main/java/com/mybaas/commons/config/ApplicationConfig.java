package com.mybaas.commons.config;

import io.vertx.core.json.JsonObject;

public interface ApplicationConfig {

    JsonObject getPlatformConfig();
    JsonObject getOptionsConfig();
    void readPlatformConfig(String config);
    void readOptionsConfig(String config);


}
