package b.contentcollector.verticles;

import b.contentcollector.targetsuppliers.MongoDbTargetSupplier;
import com.mybaas.commons.mongodb.MongoClientConfigurationException;
import com.mybaas.commons.mongodb.MongoClientFactory;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class ContentDownloaderVerticleTest {
    Vertx vertx;
    HttpServer server;
    MongoClient mongoClient;
    MongoDbTargetSupplier supplier;

    @Before
    public void before(TestContext context) throws MongoClientConfigurationException {
        vertx = Vertx.vertx();
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

    @Test
    public void testDownload(TestContext context) {
        Async async = context.async();
        vertx.deployVerticle(ContentDownloaderVerticle.class, new DeploymentOptions());
        async.complete();
    }

}