/*
 * Copyright (c)  2019 DMI inc. (http://www.dminc.com/)
 *
 *
 *
 */

package com.mybaas.commons.mongodb.abnd;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.MongoClientDeleteResult;
import io.vertx.ext.mongo.MongoClientUpdateResult;


import java.util.List;

public class MongoDbWrapper {

    private MongoClient client;

    protected MongoDbWrapper(Vertx vertx, JsonObject config) {
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




    protected void insertDocument(String collection, JsonObject document, Handler<AsyncResult<@Nullable String>> resultHandler){
        client.insert(collection, document, res -> {
            if (res.succeeded()) {
                resultHandler.handle(Future.succeededFuture(res.result()));
            }
            else if (res.failed()) {
                resultHandler.handle(Future.failedFuture(res.cause()));
            }
        });
    }



    protected void saveDocument(String collection, JsonObject document, Handler<AsyncResult<@Nullable String>> resultHandler){
        client.save(collection, document, res -> {
            if (res.succeeded()) {
                resultHandler.handle(Future.succeededFuture(res.result()));
            }
            else if (res.failed()) {
                resultHandler.handle(Future.failedFuture(res.cause()));
            }
        });
    }

    protected void updateDocuments(String collection, JsonObject query, JsonObject update,
                                   Handler<AsyncResult<MongoClientUpdateResult>> resultHandler){
        client.updateCollection(collection, query, update, res -> {
            if (res.succeeded()) {
                resultHandler.handle(Future.succeededFuture(res.result()));
            }
            else if (res.failed()) {
                resultHandler.handle(Future.failedFuture(res.cause()));
            }
        });
    }

    protected void replaceDocuments(String collection, JsonObject query, JsonObject replace, Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) {
        client.replaceDocuments(collection, query, replace, res -> {
            if (res.succeeded()) {
                resultHandler.handle(Future.succeededFuture(res.result()));
            }
            else if (res.failed()) {
                resultHandler.handle(Future.failedFuture(res.cause()));
            }
        });
    }

    protected void removeDocuments(String collection, JsonObject query, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler){
        client.removeDocuments(collection, query, res -> {
            if (res.succeeded()) {
                resultHandler.handle(Future.succeededFuture(res.result()));
            }
            else if (res.failed()) {
                resultHandler.handle(Future.failedFuture(res.cause()));
            }
        });
    }



    protected void findDocuments(String collection, JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler){
        client.find(collection, query, res -> {
            if (res.succeeded()) {
                resultHandler.handle(Future.succeededFuture(res.result()));
            }
            else if (res.failed()) {
                resultHandler.handle(Future.failedFuture(res.cause()));
            }
        });
    }

    protected void findOneDocument(String collection, JsonObject query, @Nullable JsonObject fields, Handler<AsyncResult<@Nullable JsonObject>> resultHandler) {
        client.findOne(collection, query, fields, res -> {
            if (res.succeeded()) {
                resultHandler.handle(Future.succeededFuture(res.result()));
            }
            else if (res.failed()) {
                resultHandler.handle(Future.failedFuture(res.cause()));
            }
        });
    }






}
