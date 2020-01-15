package com.mybaas.commons;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;

import java.io.File;

/**
 *Custom launcher to configure, deploy and run verticles.
 *
 * @author Bora Tashan
 */
public class Launcher extends io.vertx.core.Launcher {

    public static void main(String[] args) {
        System.out.println("#########Custom launcher");
        new Launcher().dispatch(args);
    }

    @Override
    public void beforeStartingVertx(VertxOptions options) {
        // TODO: 2019-05-28 Implement clustered configuration, currently everything on local jvm
        //options.setClustered(true)
        //        .setClusterHost("127.0.0.1");

        System.out.println("#########Custom launcher");
    }

    @Override
    public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
        super.beforeDeployingVerticle(deploymentOptions);

        System.out.println("#########Custom launcher");


        if (deploymentOptions.getConfig() == null) {
            deploymentOptions.setConfig(new JsonObject());
        }
        File configFile = new File("config.json");
        JsonObject config = new ConfigurationManager(Vertx.vertx()).getConfiguration(configFile);
        deploymentOptions.getConfig().mergeIn(config);

    }
}
