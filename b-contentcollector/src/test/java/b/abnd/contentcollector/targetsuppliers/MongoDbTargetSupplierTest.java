package b.abnd.contentcollector.targetsuppliers;


import b.commons.model.ContentTarget;
import com.mybaas.commons.mongodb.MongoClientConfigurationException;
import com.mybaas.commons.mongodb.MongoClientFactory;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.junit.*;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

@RunWith(VertxUnitRunner.class)
public class MongoDbTargetSupplierTest {

    HttpServer server;
    MongoClient mongoClient;
    MongoDbTargetSupplier supplier;

    @Before
    public void before(TestContext context) throws MongoClientConfigurationException {
        Vertx vertx = Vertx.vertx();
        mongoClient = MongoClientFactory.buildClient(vertx);
        supplier = new MongoDbTargetSupplier.Builder()
                .withSourceCollection("content-targets")
                .withMongoClient(MongoClientFactory.buildClient(vertx))
                .build();
    }

    @After
    public void after(TestContext context) {
        mongoClient.close();
    }

    @Ignore
    @Test
    public void testTargetSupplier(TestContext context) {
        Async async = context.async();
        Future<List<Optional<ContentTarget>>> res =  supplier.getAll();
        res.setHandler(list ->{
            if (list.succeeded()) {
                assertTrue(list.succeeded());
            }
            else {
                assertTrue(list.failed());
            }
            async.complete();
        });
    }

    private Future<HttpResponse<Buffer>> getHttpResponse(WebClient webClient, String absoluteURI) {
        Promise<HttpResponse<Buffer>> promise = Promise.promise();
        webClient.getAbs(absoluteURI).send(promise);
        return promise.future();
    }

    private Future<WebClient> prepareWebClient() {
        Promise<WebClient> promise = Promise.promise();
        WebClientOptions options = new WebClientOptions()
                .setKeepAlive(false);
        promise.complete(WebClient.create(Vertx.vertx(), options));
        return promise.future();
    }


    @Ignore
    @Test
    public void testWebClient(TestContext context) {
        Async async = context.async();
        prepareWebClient()
                .compose(webClient -> getHttpResponse(webClient, "http://www.google.com/"))
                .compose(bufferHttpResponse -> {
                    System.out.println(bufferHttpResponse.bodyAsString());
                    return Future.succeededFuture();
                })
                .setHandler(event -> {
                    System.out.println(event.toString());
                    async.complete();
                });

    }
}