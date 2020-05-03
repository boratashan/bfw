package b.contentanalyzer.service;

import b.commons.model.Content;
import b.commons.model.EncodeDecodeType;
import b.contentanalyzer.contentparsers.ContentParser;
import b.contentanalyzer.contentparsers.ContentParserFactory;
import b.contentanalyzer.exceptions.DataRecordNotFoundException;
import b.contentanalyzer.model.CurrencyRate;
import com.mybaas.utils.DateTimeFormat;
import com.mybaas.utils.DateTimeUtils;
import com.mybaas.utils.StringUtils;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.ext.mongo.BulkOperation;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContentParserAndAnalyzerServiceWithRestImpl implements ContentParserAndAnalyzerService {
    private static final String COLLECTION_CONTENTS  = "contents";

    private MongoClient mongoClient;
    private Logger logger;


    public ContentParserAndAnalyzerServiceWithRestImpl(MongoClient mongoClient, Logger logger) {
        this.mongoClient = mongoClient;
        this.logger = logger;
    }


    @Override
    public ContentParserAndAnalyzerService parseContent(String contentId, Handler<AsyncResult<Boolean>> resultHandler) {


        Future<JsonObject> readFuture = readContentFromApi(contentId);
        readFuture.compose(res -> {
            Content content = new Content();
            content
                    .setId(res.getString("_id"))
                    .setSize(res.getInteger("size"))
                    .setTarget(res.getString("target"))
                    .setProject(res.getString("project"))
                    .setPcode(res.getString("pcode"))
                    .setEncoding(EncodeDecodeType.valueOf(res.getString("encoding", "NONE")))
                    .setTargeturi(res.getString("targeturi"))
                    .setProcessed(res.getBoolean("processed"))
                    .setContent(res.getJsonObject("content").getString("$binary"))
                    .setDownloadtime(DateTimeUtils.fromString(res.getJsonObject("downloadtime").getString("$date"), DateTimeFormat.UTCDATETIME));
            switch (content.getEncoding()) {
                case NONE:
                    break;
                case BASE64:
                    content.setContent(StringUtils.decodeByBase64(content.getContent()));
                    break;
            }
            return this.parseAndProcessContent(content);
        })
                .compose(ar -> this.saveAnalyzeResult(ar.getKey(), ar.getRight()))
                .compose(this::deleteContentByRest)
                .setHandler(event -> {
                    if (event.succeeded()) {
                        logger.info(String.format("Content %s has processed.", event.result().getId()));
                        resultHandler.handle(Future.succeededFuture());
                    } else {
                        resultHandler.handle(Future.failedFuture(event.cause()));
                    }
                });
        return this;
    }




    private Future<JsonObject> readContentFromApi(String id) {
        Promise<JsonObject> promise = Promise.promise();

        WebClient webClient = WebClient.create(Vertx.vertx(), new WebClientOptions().setSsl(false).setUserAgent("vx3"));
        String hostToCall = String.format("/contents/%s", id);
        webClient
                .get(9300, "localhost", hostToCall)
                .send( ar -> {
                    if (ar.succeeded()) {
                        JsonObject res = ar.result().bodyAsJsonObject();
                        //String content = StringUtils.decodeByBase64(res.getString("content"));
                        promise.complete(res);
                        //logger.debug(String.format("Rest : %s,   Response : %s", String.format("http://localhost:9300/api/contents/%s", id), res.toString()));
                    }
                    else {
                        promise.fail(ar.cause());
                    }
                });
        return promise.future();
    }

    private Future<JsonObject> readContent(String id) {
        Promise<JsonObject> promise = Promise.promise();
        mongoClient.findOne("contents", new JsonObject().put("_id", id), null, r ->{
            if (r.succeeded()) {
                if (Objects.nonNull(r.result())) {
                    promise.complete(r.result());
                } else {
                    promise.fail(new DataRecordNotFoundException(String.format("Content \"%s\"not found", id)));
                }
            }
            else {
                promise.fail(r.cause());
            }
        } );
        return promise.future();
    }


    private Future<Content> saveAnalyzeResult(Content content, List<CurrencyRate> rates) {
        Promise<Content> promise = Promise.promise();
        List<BulkOperation> operations = new ArrayList<>();
        rates.stream().forEach(currencyRate -> {
            JsonObject doc = new JsonObject();
            doc
                    .put("currency", currencyRate.getCurrCode())
                    .put("datetime", DateTimeUtils.toString(currencyRate.getDatumDateTime(), DateTimeFormat.UTCDATETIME))
                    .put("buy", currencyRate.getBuy())
                    .put("sell", currencyRate.getSell())
                    .put("sourcecontent", new JsonObject()
                            .put("sourceid", content.getId())
                            .put("pcode", content.getPcode())
                            .put("downloadtime", DateTimeUtils.toString(content.getDownloadtime(), DateTimeFormat.UTCDATETIME))
                            .put("processtime", DateTimeUtils.toString(content.getProcesstime(), DateTimeFormat.UTCDATETIME))
                    );
            operations.add(BulkOperation.createInsert(doc));
        });

        mongoClient.bulkWrite("CollectedCurrencyRates", operations, ar -> {
            if (ar.succeeded()) {
                JsonObject query = new JsonObject().put("_id", content.getId());
                JsonObject update = new JsonObject().put("$set", new JsonObject().put("processed", true));
                mongoClient.updateCollection("contents", query,  update, res -> {
                    if (res.succeeded()) {
                        promise.complete(content.setProcessed(true));
                    } else {
                        promise.fail(res.cause());
                    }
                });
            }
            else {
                promise.fail(ar.cause());
            }
        });
        return promise.future();
    }



    private Future<Pair<Content, List<CurrencyRate>>> parseAndProcessContent(Content content){
        Promise<Pair<Content, List<CurrencyRate>>> promise = Promise.promise();
        ContentParser parser = ContentParserFactory.getParser(content, mongoClient);
        parser.parse(content, event -> {
            if(event.succeeded()){
                System.out.println(event.result().toString());
                promise.complete(Pair.of(content, event.result()));
            }
            else {
                promise.fail(event.cause());
            }
        });
        return promise.future();
    }

    private Future<Content> deleteContent(Content content) {
        Promise<Content> promise = Promise.promise();
        mongoClient.findOneAndDelete("contents", new JsonObject().put("_id", content.getId()), ar -> {
            if (ar.succeeded()) {
                promise.complete(content);
            }
            else {
                promise.fail(ar.cause());
            }
        });
        return promise.future();
    }

    private Future<Content> deleteContentByRest(Content content) {
        Promise<Content> promise = Promise.promise();
        WebClient webClient = WebClient.create(Vertx.vertx(), new WebClientOptions().setSsl(false).setUserAgent("vx3"));
        String hostToCall = String.format("/contents/%s", content.getId());
        webClient
                .delete(9300, "localhost", hostToCall)
                .send( ar -> {
                    if (ar.succeeded()) {
                        JsonObject res = ar.result().bodyAsJsonObject();
                        //String content = StringUtils.decodeByBase64(res.getString("content"));
                        promise.complete(content);
                        //logger.debug(String.format("Rest : %s,   Response : %s", String.format("http://localhost:9300/api/contents/%s", id), res.toString()));
                    }
                    else {
                        promise.fail(ar.cause());
                    }
                });
        return promise.future();
    }



}
