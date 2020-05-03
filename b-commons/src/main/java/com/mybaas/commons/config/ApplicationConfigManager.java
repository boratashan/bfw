package com.mybaas.commons.config;

import com.mybaas.commons.exceptions.ApplicationInitializationException;
import com.mybaas.utils.ResourceUtils;

import java.io.IOException;
import java.util.Objects;

public class ApplicationConfigManager {

    private ApplicationConfig config;

    private static ApplicationConfigManager INSTANCE;

    public synchronized static ApplicationConfigManager init(String platformConfig, String optionsConfig){
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new ApplicationConfigManager();
            INSTANCE.config = new DefaultApplicationConfig();
            INSTANCE.config.readOptionsConfig(optionsConfig);
            INSTANCE.config.readPlatformConfig(platformConfig);
        }
        return INSTANCE;
    }

    public synchronized static ApplicationConfigManager initFromResources() throws IOException {
        String platform = ResourceUtils.getResourceAsString(ApplicationConfigConstants.CONFIG_EMBEDDED_PLATFORM);
        String options = ResourceUtils.getResourceAsString(ApplicationConfigConstants.CONFIG_EMBEDDED_OPTIONS);
        INSTANCE = null;
        return init(platform, options);
    }

    public static ApplicationConfig get() throws ApplicationInitializationException {
        if (Objects.isNull(INSTANCE)) {
            throw new ApplicationInitializationException("Application config has not initialized yet,  Please first init configuration!");
        } else {
            return INSTANCE.config;
        }

    }
}
