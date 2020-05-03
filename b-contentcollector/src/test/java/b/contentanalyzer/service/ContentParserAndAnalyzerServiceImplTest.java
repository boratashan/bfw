package b.contentanalyzer.service;

import com.mybaas.commons.config.ApplicationConfigManager;
import com.mybaas.commons.mongodb.MongoClientConfigurationException;
import com.mybaas.commons.mongodb.MongoClientFactory;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.*;
@RunWith(VertxUnitRunner.class)
public class ContentParserAndAnalyzerServiceImplTest {

    ContentParserAndAnalyzerService service;
    MongoClient mongoClient;
    Logger logger = LoggerFactory.getLogger("Test");



    @BeforeClass
    public static void setup(TestContext context) throws MongoClientConfigurationException, IOException {
        ApplicationConfigManager.initFromResources();

    }


    @Before
    public void setUp(TestContext context) throws Exception {
        mongoClient = MongoClientFactory.buildClient(Vertx.vertx());

        service = ContentParserAndAnalyzerService.create(mongoClient, logger);

    }

    @After
    public void tearDown(TestContext context) throws Exception {
        mongoClient.close();
    }

    @Test
    public void parseContent(TestContext context) {
        Async async = context.async();
        service.parseContent("5ea1a110da557c2b1f8c43f5", event -> {
           if (event.succeeded()) {
               System.out.println(event.result());
               async.complete();
           }
           else {
               System.out.println(event.cause());
               async.complete();
           }
        });

    }
}