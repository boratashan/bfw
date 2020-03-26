package com.mybaas.commons.config;

import io.vertx.core.json.JsonObject;

public class DefaultApplicationConfig implements ApplicationConfig {


    private JsonObject configJson;
    private JsonObject optionsJson;


    public DefaultApplicationConfig() {
    }

    @Override
    public JsonObject getPlatformConfig() {
        return configJson;
    }

    @Override
    public JsonObject getOptionsConfig() {
        return optionsJson;
    }

    @Override
    public void readPlatformConfig(String config) {
        configJson = new JsonObject(config);
    }

    @Override
    public void readOptionsConfig(String config) {
        optionsJson = new JsonObject(config);
    }
}
