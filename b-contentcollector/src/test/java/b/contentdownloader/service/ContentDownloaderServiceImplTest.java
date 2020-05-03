package b.contentdownloader.service;

import com.mybaas.commons.config.ApplicationConfigManager;
import com.mybaas.commons.mongodb.MongoClientConfigurationException;
import com.mybaas.commons.mongodb.MongoClientFactory;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.serviceproxy.ServiceBinder;
import org.junit.*;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(VertxUnitRunner.class)
public class ContentDownloaderServiceImplTest {

    private static final String EB_SVC_ADD = "ebs.ContentDownloaderService";
    MongoClient mongoClient;
    ContentDownloaderService downloaderService;
    Vertx vertx;
    MessageConsumer consumer;

    @BeforeClass
    public static void setup(TestContext context) throws MongoClientConfigurationException, IOException {
        ApplicationConfigManager.initFromResources();

    }



    @Before
    public void tearUp(TestContext context) throws MongoClientConfigurationException {
        vertx = Vertx.vertx();
        mongoClient = MongoClientFactory.buildClient(vertx);
        downloaderService = ContentDownloaderService.create(mongoClient);
        ServiceBinder binder = new ServiceBinder(vertx);
        consumer = binder.setAddress(EB_SVC_ADD).register(ContentDownloaderService.class, downloaderService);
    }

    @After
    public void tearDown(TestContext context) throws MongoClientConfigurationException {
        consumer.unregister();
        mongoClient.close();
        Object o = context.asyncAssertSuccess();
        vertx.close((Handler<AsyncResult<Void>>) o);
    }




    @Test
    public void getNumberOfDownloadedContent(TestContext context) {
        Async async = context.async();
        downloaderService.getNumberOfDownloadedContent(event -> {
            if (event.succeeded()) {
                System.out.println(String.format("Number of contents is %d ", event.result()));
            }
            else {
                System.out.println(event.cause());
            }
            async.complete();
        });
    }

    @Test
    public void getEbNumberOfDownloadedContent(TestContext context) {
        Async async = context.async();
        ContentDownloaderServiceVertxEBProxy proxy = new ContentDownloaderServiceVertxEBProxy(vertx, EB_SVC_ADD);

        proxy.getNumberOfDownloadedContent(event -> {
            if (event.succeeded()) {
                System.out.println(String.format("Number of contents is %d ", event.result()));
            }
            else {
                System.out.println(event.cause());
            }
            async.complete();
        });
    }

    @Test
    public void getListOfContentIDs() {
    }

    @Test
    public void getContentData() {
    }
}