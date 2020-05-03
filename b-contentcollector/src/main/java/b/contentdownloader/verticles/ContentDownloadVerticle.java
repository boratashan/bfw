package b.contentdownloader.verticles;


import b.commons.model.Content;
import b.commons.model.ContentTarget;
import b.commons.model.EncodeDecodeType;
import b.contentdownloader.adapters.MongoDbStorageAdapter;
import b.contentdownloader.adapters.StorageAdapter;
import com.mybaas.commons.BaseMicroServiceVerticle;
import com.mybaas.commons.mongodb.MongoClientFactory;
import io.vertx.amqp.*;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

public class ContentDownloadVerticle extends BaseMicroServiceVerticle {

    private static final String EB_DOWNLOAD_REQUEST = "download.request";
    private static final String EB_ANALYZE_REQUEST = "analyze.request";
    private static final String MQ_DOWNLOAD_COMPLETE = "queue.content.download.completed";
    AmqpConnection amqpConnection;
    private MongoClient mongoClient;
    private WebClient webClient;
    private StorageAdapter adapter;
    private MessageConsumer<ContentTarget> consumer;
    private AmqpClient amqpClient;
    private AmqpSender amqpSender;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        getLogger().info("################################################################################");
        getLogger().info("Starting ContentDownloadVerticle");
        mongoClient = MongoClientFactory.buildClient(vertx);
        WebClientOptions options = new WebClientOptions()
                .setKeepAlive(false);
        webClient = WebClient.create(vertx, options);
        adapter = new MongoDbStorageAdapter.Builder()
                .withEncodeDecodeType(EncodeDecodeType.BASE64)
                .withMongoClient(mongoClient)
                .build();


        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setConfig(config());

        setupEventBus()
                .compose( aVoid -> setupAmqpClient())
                .setHandler(event -> {
                    if (event.succeeded()) {
                        startPromise.complete();
                    }
                    else {
                        startPromise.fail(event.cause());
                    }
                });

    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        Promise<Void> pA = Promise.promise();
        Promise<Void> pC = Promise.promise();

        CompositeFuture f = CompositeFuture.all(pA.future(), pC.future());
        amqpClient.close(pA);
        consumer.unregister(pC);
        f.setHandler(event -> {
            if (event.succeeded()) {
                stopPromise.complete();
            } else {
                stopPromise.fail(event.cause());
            }
        });
    }

    private Future<Void> setupEventBus() {
        Promise<Void> promise = Promise.promise();
        consumer = vertx.eventBus().consumer(EB_DOWNLOAD_REQUEST);
        consumer.completionHandler(ar -> {
            if (ar.succeeded()) {
                System.out.println("The handler registration has reached all nodes");
                promise.complete();

            } else {
                System.out.println("Registration failed!");
                promise.fail(ar.cause());
            }
        });
        consumer.handler(this::messageHandler);
        return promise.future();
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
                        .createSender(MQ_DOWNLOAD_COMPLETE, event1 -> {
                            if (event1.succeeded()) {
                                amqpSender = event1.result();
                                promise.complete();
                            } else {
                                promise.fail(event1.cause());
                            }
                        });
            }
        });

        return promise.future();
    }

    private void messageHandler(Message<ContentTarget> message) {
        getLogger().debug(String.format("Message received -> [%s]", message.toString()));
        ContentTarget target = message.body();
        this.doDownload(target, ar -> {
            if (ar.succeeded()) {
                message.reply(new JsonObject());
            } else {
                message.fail(0, ar.cause().getMessage());
            }
        });

    }

    private Future<HttpResponse<Buffer>> getHttpResponse(WebClient webClient, ContentTarget target) {
        Promise<HttpResponse<Buffer>> promise = Promise.promise();
        try {
            webClient.getAbs(target.getTargeturi()).send(promise);
        } catch (Exception e) {
            promise.fail(e);
        }
        return promise.future();
    }


    private void doDownload(ContentTarget target, Handler<AsyncResult<Content>> resultHandler) {
        (getHttpResponse(this.webClient, target)
                .compose(event -> adapter.saveResponse(event, target)))
                .setHandler(event -> {
                    if (event.succeeded()) {
                        Content content = event.result();
                        String log = String.format("Content %s has downloaded and stored into database by ref %s", content.getTargeturi(), content.getId());
                        getLogger().info(log);
                        getLogger().debug("Sending message to Amqp Queue...");
                        sendMessageToMQ(event.result().getId(), log);
                        getLogger().debug("Sending message to Amqp Queue [DONE]");
                        resultHandler.handle(Future.succeededFuture(content));
                    } else {
                        resultHandler.handle(Future.failedFuture(event.cause()));
                    }
                });
    }


    private void sendMessageToMQ(String contentId, String message) {
        AmqpMessageBuilder messageBuilder = AmqpMessage.create();
        AmqpMessage m3 = messageBuilder
                .withBody(message)
                .subject("ContentDownload")
                .ttl(10000)
                .applicationProperties(new JsonObject().put("contentid", contentId))
                .durable(true)
                .build();
        amqpSender.send(m3);
    }

}
