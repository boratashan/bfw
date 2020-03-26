package b.contentcollector.verticles;

import b.contentanalyzer.verticles.ContentAnalyzerVerticle;
import b.contentcollector.adapters.EncodeDecodeType;
import b.contentcollector.adapters.MongoDbStorageAdapter;
import b.contentcollector.adapters.StorageAdapter;
import b.contentcollector.model.Content;
import b.contentcollector.model.ContentTarget;
import b.contentcollector.targetsuppliers.MongoDbTargetSupplier;
import com.mybaas.AppRunner;
import com.mybaas.commons.BaseMicroServiceVerticle;
import com.mybaas.commons.mongodb.MongoClientFactory;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ContentDownloaderVerticle extends BaseMicroServiceVerticle {
    private MongoClient mongoClient;
    private WebClient webClient;
    private MongoDbTargetSupplier supplier;
    private StorageAdapter adapter;
    private long periodicTimerID;

    private Long getDownloadPeriod() {
        return config().getLong("download.periodinsecs", 60L) * 1000;
    }

    private String getEvbDownloadAnnounceChannelName() {
        return config().getString("download.anouncechannel", "undefined");
    }

    private void deployOthers() {
        getLogger().info("Deploying other verticles...");
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setConfig(config());
        vertx.deployVerticle(ContentAnalyzerVerticle::new, deploymentOptions);
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        getLogger().info("################################################################################");
        getLogger().info("Starting ContentDownloader");
        deployOthers();
        mongoClient = MongoClientFactory.buildClient(vertx);
        WebClientOptions options = new WebClientOptions()
                .setKeepAlive(false);
        webClient = WebClient.create(vertx, options);
        supplier = new MongoDbTargetSupplier.Builder()
                .withSourceCollection("content-targets")
                .withMongoClient(MongoClientFactory.buildClient(vertx))
                .build();
        adapter = new MongoDbStorageAdapter.Builder()
                .withEncodeDecodeType(EncodeDecodeType.BASE64)
                .withMongoClient(mongoClient)
                .build();
        getLogger().debug("Setup periodic download...");
        getLogger().debug("Run periodic task for the first time.");
        this.startDownload();
        getLogger().debug("Run periodic task for the first time[DONE]");
        getLogger().debug(String.format("Setup periodic timer - period is %d seconds", getDownloadPeriod()/60));
        periodicTimerID = vertx.setPeriodic(getDownloadPeriod(), event -> {
            getLogger().debug("Start periodic task...");
            this.startDownload();
            getLogger().debug("Start periodic task [DONE]");
        });
        getLogger().debug("Setup periodic download [DONE]");
        getLogger().info("Starting ContentDownloader [DONE]");
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        getLogger().info("Stopping ContentDownloader");
        getLogger().debug("Stopping periodic timer...");
        vertx.cancelTimer(periodicTimerID);
        getLogger().debug("Stopping periodic timer[DONE]");
        getLogger().info("Stopping ContentDownloader[DONE]");
    }

    private void startDownload() {
        getLogger().debug("Starting download");
        Future<List<Optional<ContentTarget>>> targets =  supplier.getAll();
        targets.setHandler(ar -> {
           if (ar.succeeded()) {
               for (Optional<ContentTarget> t : ar.result()) {
                   getLogger().debug(String.format("Do download content-target [%s]", t.get().toString()));
                   this.doDownload(t.get(), event -> {
                       if (event.succeeded()) {
                           getLogger().debug(String.format("doDownload is done, contentId is : \"%s\"", event.result().getId()));
                       }
                       else {
                           getLogger().error("Error in doDownload", ar.cause());
                       }
                   } );
               }
           }
           else {
                getLogger().error(String.format("Exception im GetAll Targets, message : %s", ar.cause().getMessage()));
                getLogger().debug(ar.cause().getMessage(), ar.cause());
           }
        });
    }

    private void doDownload(ContentTarget target, Handler<AsyncResult<Content>> resultHandler){
        (getHttpResponse(this.webClient, target)
                .compose(event -> adapter.saveResponse(event, target)))
                .setHandler( event ->  {
                    if (event.succeeded()) {
                        Content content = event.result();
                        String channelName = getEvbDownloadAnnounceChannelName();
                        getLogger().info(String.format("Content %s has downloaded and stored into database by ref %s", content.getTargeturi(), content.getId()));
                        getLogger().debug(String.format("EVENT-BUS, sending message %s to channel %s", content, channelName));
                        vertx.eventBus().send(channelName, event.result().getId());
                        resultHandler.handle(Future.succeededFuture(content));
                    } else {
                        resultHandler.handle(Future.failedFuture(event.cause()));
                    }
        });
    }

    private Future<HttpResponse<Buffer>> getHttpResponse(WebClient webClient, ContentTarget target) {
        Promise<HttpResponse<Buffer>> promise = Promise.promise();
        try {
            webClient.getAbs(target.getTargeturi()).send(promise);
        }catch (Exception e) {
            promise.fail(e);
        }
        return promise.future();
    }
}
