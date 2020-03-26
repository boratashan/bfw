/*
 * Copyright (c)  2019 DMI inc. (http://www.dminc.com/)
 *
 *
 *
 */

package com.mybaas.commons;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


/**
 * This class is responsible to provide configuration depends on its own configuration(clustered, classpath, redis or git etc...)
 *
 * @author Bora Tashan
 */
public class ConfigurationManager {

    protected ConfigRetriever retriever;

    public ConfigurationManager(Vertx vertx) {
        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setOptional(true)
                .setConfig(new JsonObject().put("path", "config.json"));
        ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(fileStore);
        retriever = ConfigRetriever.create(vertx, options);

    }

    public JsonObject getConfiguration(File config) {
        JsonObject conf = new JsonObject();
        if (config.isFile()) {
            System.out.println("Reading config file: " + config.getAbsolutePath());
            try (Scanner scanner = new Scanner(config).useDelimiter("\\A")) {
                String sconf = scanner.next();
                try {
                    conf = new JsonObject(sconf);
                } catch (DecodeException e) {
                    System.err.println("Configuration file " + sconf + " does not contain a valid JSON object");
                }
            } catch (FileNotFoundException e) {
                // Ignore it.
            }
        } else {
            System.out.println("Config file is not found " + config.getAbsolutePath());
        }
        System.out.println("Config retriever Success : " + conf.toString());
        return conf;
    }

    public void readConfig(Vertx vertx, Handler<AsyncResult<JsonObject>> resultHandler) {
        retriever.getConfig(ar -> {
            if(ar.failed()) {
               resultHandler.handle(Future.failedFuture(ar.cause()));
            }
            else {
                resultHandler.handle(Future.succeededFuture(ar.result()));
            }
        });
    }
}
