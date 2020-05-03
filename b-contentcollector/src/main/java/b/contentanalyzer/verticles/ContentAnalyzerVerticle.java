package b.contentanalyzer.verticles;


import b.commons.model.Content;
import b.commons.model.EncodeDecodeType;
import b.contentanalyzer.exceptions.DataRecordNotFoundException;
import b.contentanalyzer.model.CurrencyRate;
import b.contentanalyzer.contentparsers.ContentParser;
import b.contentanalyzer.contentparsers.ContentParserFactory;
import com.mybaas.utils.DateTimeFormat;
import com.mybaas.utils.DateTimeUtils;
import com.mybaas.utils.StringUtils;
import com.mybaas.commons.BaseMicroServiceVerticle;
import com.mybaas.commons.mongodb.MongoClientFactory;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContentAnalyzerVerticle extends BaseMicroServiceVerticle {

    private MongoClient mongoClient;
    private long periodicTimerID;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        getLogger().info("Starting Content Analyzer");
        getLogger().debug("Obtaining mongo client");
        mongoClient = MongoClientFactory.buildClient(vertx);
        getLogger().debug("Start traversing unprocessed contents");
        vertx.eventBus().consumer(getEvbDownloadAnnounceChannelName(), ar-> {
            if(Objects.nonNull(ar.body())) {
                String id = ar.body().toString();
                getLogger().debug(String.format("Received message [%s] from channel [%s]", id, ar.address()));
                this.analyzeContent(id);
            }
        });
        registerAndStartPeriodicTask();
        getLogger().info("Starting Content Analyzer [DONE]");

    }

    private String getEvbDownloadAnnounceChannelName() {
        return config().getString("download.anouncechannel", "undefined");
    }

    private Long getAnalyzePeriod() {
        return config().getLong("analyzer.periodinsecs", 30L) * 1000;
    }



    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        getLogger().info("Stopping Content Analyzer...");
        getLogger().debug("Stopping periodic timer...");
        vertx.cancelTimer(periodicTimerID);
        getLogger().debug("Stopping periodic timer[DONE]");
        getLogger().info("Stopping Content Analyzer [DONE]");
    }

    private void registerAndStartPeriodicTask() {
        this.periodicTimerID = vertx.setPeriodic(this.getAnalyzePeriod(), ar ->{
            JsonObject query = new JsonObject();
            query.put("processed", false);
            mongoClient.findBatch("contents", query)
                    .handler(content -> {
                        String id = content.getJsonObject("_id").getString("$oid");
                        getLogger().debug(String.format("Analyzing content id \"%s\"", id));
                        this.analyzeContent(id);
                        getLogger().debug(String.format("Content \"%s\" is analyzed [DONE]", id));
                    })
                    .exceptionHandler(event ->  {
                        getLogger().error("Exception in startTraversingUnprocessedContents", event);
                    })
                    .endHandler(event -> {
                        getLogger().debug("Finished startTraversingUnprocessedContents");
                    });
        });
    }


    private void analyzeContent(String id) {
        Future<JsonObject> readFuture = readContent(id);
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
                .compose(this::deleteContent)
                .setHandler(event -> {
                    if (event.succeeded()) {
                        getLogger().info(String.format("Read, Analyzed and Deleted content \"%s\"", event.result().getId()));
                    } else {
                        getLogger().error("An error has occured in analyzing content ", event.cause());
                    }
                });
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



}
