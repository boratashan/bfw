package b.contentanalyzer.verticles;

import b.contentanalyzer.service.ContentParserAndAnalyzerService;
import b.contentdownloader.service.ContentDownloaderService;
import com.mybaas.commons.BaseRestAPIVerticle;
import com.mybaas.commons.mongodb.MongoClientFactory;
import io.vertx.amqp.*;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;

public class ContentParserAndAnalyzerVerticle extends BaseRestAPIVerticle {
    //FIXME  look later
    private static final String EB_ANALYZE_REQUEST = "analyze.request";

    private static final String MQ_DOWNLOAD_COMPLETE = "queue.content.download.completed";

    private Router router;
    private MongoClient mongoClient;
    private AmqpClient amqpClient;
    private AmqpConnection amqpConnection;
    private AmqpReceiver amqpReceiver;
    private ContentParserAndAnalyzerService service;


    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        mongoClient = MongoClientFactory.buildClient(vertx);
        service = ContentParserAndAnalyzerService.create(mongoClient, getLogger());
        //TODO add activemq listener
        setupAmqpClient().setHandler(startPromise);

    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        mongoClient.close();
        stopPromise.complete();
    }

    private Future<Void> setupAmqpClient() {
        Promise<Void> promise = Promise.promise();
        AmqpClientOptions amqpClientOptions = new AmqpClientOptions()
                .setHost("127.0.0.1")
                .setPort(5672)
                .setUsername("admin")
                .setPassword("admin");
        amqpClient = AmqpClient.create(vertx, amqpClientOptions);
        amqpClient.connect(ar -> {
            if (ar.failed()) {
                System.out.println("Unable to connect to the broker");
                promise.fail(ar.cause());
            } else {
                System.out.println("Connection succeeded");
                amqpConnection = ar.result();
                amqpConnection
                        .createReceiver(MQ_DOWNLOAD_COMPLETE, event1 -> {
                            if (event1.succeeded()) {
                                amqpReceiver = event1.result();
                                amqpReceiver.handler(this::mqReceiverHandler);
                                promise.complete();
                            } else {
                                promise.fail(event1.cause());
                            }
                        });
            }
        });

        return promise.future();
    }

    private void mqReceiverHandler(AmqpMessage amqpMessage) {
        String body = amqpMessage.bodyAsString();
        getLogger().debug(String.format("MQ - Message received : %s ", body));
        JsonObject prop = amqpMessage.applicationProperties();
        String contentId = prop.getString("contentid");
        service.parseContent(contentId, res -> {
            if (res.succeeded()) {
                getLogger().debug(String.format("ContentService.Parsing Done : %s ", res.result()));
            }
            else {
                getLogger().debug(String.format("ContentService.Parsing error : %s ", res.cause().getMessage()));

            }
        });
        getLogger().debug(String.format("MQ - Message received : %s ", contentId));
        amqpMessage.accepted();
    }


}
