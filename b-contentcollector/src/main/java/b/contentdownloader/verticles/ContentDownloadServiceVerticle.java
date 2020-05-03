package b.contentdownloader.verticles;

import b.contentdownloader.service.ContentDownloaderService;
import com.mybaas.commons.BaseMicroServiceVerticle;
import com.mybaas.commons.mongodb.MongoClientFactory;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ServiceBinder;

public class ContentDownloadServiceVerticle extends BaseMicroServiceVerticle {

    private static final String EB_SERVICE_ADDRESS = "ContentDownloadService.queue";


    private ServiceBinder binder;
    private MongoClient mongoClient;
    private ContentDownloaderService service;
    private MessageConsumer messageConsumer;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        mongoClient = MongoClientFactory.buildClient(vertx);
        service = ContentDownloaderService.create(mongoClient);

        binder = new ServiceBinder(vertx);
        messageConsumer = binder
                .setAddress(EB_SERVICE_ADDRESS)
                .register(ContentDownloaderService.class, service);

    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        messageConsumer.unregister();

    }
}
