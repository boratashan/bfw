package b.contentdownloader.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;

import java.util.List;

public class ContentDownloaderServiceImpl implements ContentDownloaderService {

    private static final String COLLECTION_CONTENTS  = "contents";
    private MongoClient mongoClient;

    public ContentDownloaderServiceImpl(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public ContentDownloaderService getNumberOfDownloadedContent(Handler<AsyncResult<Long>> resultHandler) {
        JsonObject query = new JsonObject();
        mongoClient.count(COLLECTION_CONTENTS,  query, resultHandler);
        return this;
    }

    @Override
    public ContentDownloaderService getListOfContentIDs(int limit, int offset, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
        JsonObject query = new JsonObject();
        FindOptions options = new FindOptions();
        options.setLimit(limit).setSkip(offset).setFields(new JsonObject().put("_id", "1"));

        mongoClient.findWithOptions(COLLECTION_CONTENTS, query, options, resultHandler);
        return this;
    }

    @Override
    public ContentDownloaderService getContentData(String contentID, Handler<AsyncResult<JsonObject>> resultHandler) {
        JsonObject query = new JsonObject();
        query.put("_id", contentID);
        //new JsonObject().put("content", "1")
        mongoClient.findOne(COLLECTION_CONTENTS,  query, null, resultHandler);
        return this;
    }

    @Override
    public ContentDownloaderService deleteContentData(String contentID, Handler<AsyncResult<JsonObject>> resultHandler) {
        JsonObject query = new JsonObject();
        query.put("_id", contentID);
        mongoClient.findOneAndDelete(COLLECTION_CONTENTS,  query,  resultHandler);
        return this;

    }
}
