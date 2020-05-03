package b.contentdownloader.service;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.List;

@VertxGen
@ProxyGen
public interface ContentDownloaderService {
    static ContentDownloaderService create(MongoClient mongoClient) {
        return new ContentDownloaderServiceImpl(mongoClient);
    }
    @Fluent
    ContentDownloaderService getNumberOfDownloadedContent(Handler<AsyncResult<Long>> resultHandler);
    @Fluent
    ContentDownloaderService getListOfContentIDs(int limit, int offset, Handler<AsyncResult<List<JsonObject>>> resultHandler);
    @Fluent
    ContentDownloaderService getContentData(String contentID, Handler<AsyncResult<JsonObject>> resultHandler);
    @Fluent
    ContentDownloaderService deleteContentData(String contentID, Handler<AsyncResult<JsonObject>> resultHandler);
}
