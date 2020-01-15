/*
 * Copyright (c)  2019 DMI inc. (http://www.dminc.com/)
 *
 *
 *
 */

package com.mybaas.commons.data;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.List;

public class MongoDbWrapper {

    private MongoClient client;

    protected MongoDbWrapper(Vertx vertx,  JsonObject config) {
        this.client = MongoClient.createNonShared(vertx, config);
    }

    protected void createCollection(String collectionName, Handler<AsyncResult<Void>> resultHandler) {
        client.createCollection(collectionName, res -> {
            if (res.succeeded()) {
                resultHandler.handle(Future.succeededFuture());
            }
            else {
                resultHandler.handle(Future.failedFuture(res.cause()));
            }
        });
    }

    protected void deleteCollection(String collectionName, Handler<AsyncResult<Void>> resultHandler) {
        client.dropCollection(collectionName, res -> {
            if (res.succeeded()) {
                resultHandler.handle(Future.succeededFuture());
            }
            else {
                resultHandler.handle(Future.failedFuture(res.cause()));
            }
        });
    }

    protected void getCollections(Handler<AsyncResult<List<String>>> resultHandler) {
        client.getCollections(res -> {
            if (res.succeeded()) {
                resultHandler.handle(Future.succeededFuture(res.result()));
            }
            else {
                resultHandler.handle(Future.failedFuture(res.cause()));
            }
        });
    }



}
