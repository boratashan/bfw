/*
 * Copyright (c)  2019 DMI inc. (http://www.dminc.com/)
 *
 *
 *
 */

package com.mybaas.commons.data;

import io.vertx.core.json.JsonObject;

public class MongoClientConfig {

    private static final String CONNECTION_STRING_TEMPLATE = "mongodb://%s:%d";

    private String dbName;
    private boolean useObjectId = false;
    private String host;
    private int port;
    private String userName;
    private String password;


    public MongoClientConfig() {

    }

    public static MongoClientConfig create() {
        return new MongoClientConfig();
    }

    public String getDbName() {
        return dbName;
    }

    public MongoClientConfig setDbName(String dbName) {
        this.dbName = dbName;
        return this;
    }

    public boolean isUseObjectId() {
        return useObjectId;
    }

    public MongoClientConfig setUseObjectId(boolean useObjectId) {
        this.useObjectId = useObjectId;
        return this;
    }

    public String getHost() {
        return host;
    }

    public MongoClientConfig setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public MongoClientConfig setPort(int port) {
        this.port = port;
        return this;
    }

    public String getConnectionString() {
        return String.format(CONNECTION_STRING_TEMPLATE, this.getHost(), this.getPort());
    }
    public String getUserName() {
        return userName;
    }

    public MongoClientConfig setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public MongoClientConfig setPassword(String password) {
        this.password = password;
        return this;
    }



    public JsonObject toJson() {
        return new JsonObject()
                .put("db_name", this.dbName)
                .put("useObjectId", this.useObjectId)
                .put("connection_string", this.getConnectionString())
                .put("username", this.userName)
                .put("password", this.password);
    }

}
