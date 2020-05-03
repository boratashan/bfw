package b.contentanalyzer.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.ext.mongo.MongoClient;

public interface ContentParserAndAnalyzerService {

    public static ContentParserAndAnalyzerService create(MongoClient mongoClient, Logger logger) {
        return new ContentParserAndAnalyzerServiceWithRestImpl(mongoClient, logger);
    }

    ContentParserAndAnalyzerService  parseContent(String contentId, Handler<AsyncResult<Boolean>> resultHandler);

}
