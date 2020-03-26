package b.currencycrawler.verticles;


import b.currencycrawler.api.CrawlerApiVerticle;
import b.currencycrawler.crawler.CurrencyRateCrawler;
import b.currencycrawler.crawler.CurrencyRateCrawlerException;
import b.currencycrawler.crawler.DefaultCurrencyRateCrawler;
import com.mybaas.commons.mongodb.MongoClientFactory;
import b.currencycrawler.model.CurrencyRate;
import b.currencycrawler.model.CurrencyRates;
import b.currencycrawler.services.CurrencyRatesService;
import b.currencycrawler.services.impl.CurrencyRateServiceImpl;
import com.mybaas.commons.BaseMicroServiceVerticle;
import io.vertx.core.*;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ServiceBinder;


import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CurrencyGeneratorVerticle extends BaseMicroServiceVerticle {

    public static final String EVB_ADDRESS_CURRENCYRATES = "CurrencyRates-Announcing";
    public static final String THREAD_WORKER_SHARED_NAME_CURRENCYRATES = "CurrencyRates-Pool";
    public static final int TIMER_PERIOD_CURRENCYRATES_CRAWLING_IN_SECS = 50;

    private ServiceBinder serviceBinder;
    private MessageConsumer<JsonObject> messageConsumer;
    private CurrencyRatesService currencyRatesService;
    private CurrencyRateCrawler crawler = new DefaultCurrencyRateCrawler();
    private MongoClient mongoClient;


    @Override
    public void start() throws Exception {
        super.start();

            mongoClient = MongoClientFactory.buildClient(vertx);


            currencyRatesService = new CurrencyRateServiceImpl(vertx);
            serviceBinder = new ServiceBinder(vertx);
            messageConsumer = serviceBinder
                    .setAddress(CurrencyRatesService.SERVICE_ADDRESS)
                    .register(CurrencyRatesService.class, currencyRatesService);

            publishMessageSource(this.EVB_ADDRESS_CURRENCYRATES, this.EVB_ADDRESS_CURRENCYRATES);
            vertx.deployVerticle(new CrawlerApiVerticle(currencyRatesService));
            runCrawlerPeriodicTask();
    }

    @Override
    public void stop(Future<Void> future) throws Exception {
        super.stop(future);
        mongoClient.close();
        serviceBinder.unregister(messageConsumer);
    }

    public void consumeProxy() {
  /*      CurrencyRatesServiceVertxEBProxy proxy = new CurrencyRatesServiceVertxEBProxy(vertx, CurrencyRatesService.SERVICE_ADDRESS);
        proxy.getLatestCurrencyRates(res -> {
            if (res.succeeded()) {
                System.out.println(res.result().toString());
            } else if (res.failed()) {
                System.out.println(res.cause().getMessage());
            }
        });

   */
    }



    private void runCrawlerPeriodicTask() {
        vertx.setTimer(TimeUnit.SECONDS.toMillis(5), event -> {
            vertx.executeBlocking(future -> {
                try {
                    List<CurrencyRate> rates = this.crawlRates();
                    this.writeRatesIntoDocumentDB(rates);
                    this.sendToEventBus(rates);
                    future.complete("Success");
                } catch (CurrencyRateCrawlerException e) {
                    future.fail(e);
                }
            }, res -> {
                if (res.succeeded()) {
                    System.out.println(String.format("Currency crawler has finished crawling succesfully, Result : [%s]",
                            res.result()));
                } else if (res.failed()) {
                    System.out.println(String.format("Currency crawler has Failed, Result : [%s]",
                            res.cause().getMessage()));
                }
                ;
            });
        });
        vertx.setPeriodic(TimeUnit.SECONDS.toMillis(TIMER_PERIOD_CURRENCYRATES_CRAWLING_IN_SECS), event -> {
            vertx.executeBlocking(future -> {
                try {
                    List<CurrencyRate> rates = this.crawlRates();
                    this.writeRatesIntoDocumentDB(rates);
                    this.sendToEventBus(rates);
                    future.complete("Success");
                } catch (CurrencyRateCrawlerException e) {
                    future.fail(e);
                }
            }, res -> {
                if (res.succeeded()) {
                    System.out.println(String.format("Currency crawler has finished crawling succesfully, Result : [%s]",
                            res.result()));
                } else if (res.failed()) {
                    System.out.println(String.format("Currency crawler has Failed, Result : [%s]",
                            res.cause().getMessage()));
                }
                ;
            });
        });
    }

    private List<CurrencyRate> crawlRates() throws CurrencyRateCrawlerException {
        return crawler.buildRates();
    }

    private void writeRatesIntoDocumentDB(List<CurrencyRate> rates) {
        CurrencyRates currRatesDoc = new CurrencyRates();
        currRatesDoc.setDateTime(LocalDateTime.now().toString());
        currRatesDoc.setSource("www.doviz.com");
        currRatesDoc.setCurrencyRateList(rates);

        this.currencyRatesService.writeAndCacheRatesToDb(currRatesDoc, r -> {
            if (r.succeeded()) {
                System.out.println(r.result());
            } else {
                System.out.println(r.cause());
            }
        });
        /*

        JsonObject jsonObject = currRatesDoc.toJson();
        Promise<String> promise = Promise.promise();
        promise.future().setHandler(r -> {
            if (r.succeeded()) {
                System.out.println(r.result());
            } else {
                System.out.println(r.cause());
            }
        });
        mongoClient.insert("Rates", jsonObject, res -> {
            if (res.succeeded()) {
                promise.complete(res.result());
            } else if (res.failed()) {
                promise.fail(res.cause());
            }
        });*/
    }




    private void sendToEventBus(List<CurrencyRate> currencyRates) {
        JsonArray array = new JsonArray(currencyRates);
        vertx.eventBus().publish(EVB_ADDRESS_CURRENCYRATES, array);
    }

}
