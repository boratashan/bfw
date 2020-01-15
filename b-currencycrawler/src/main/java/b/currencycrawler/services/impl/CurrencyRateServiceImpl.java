package b.currencycrawler.services.impl;

import b.currencycrawler.amqpclient.AmqpClientConfigurationException;
import b.currencycrawler.amqpclient.AmqpClientFactory;
import b.currencycrawler.caching.infinispan.InfinispanCacheConfigurationException;
import b.currencycrawler.caching.infinispan.InfinispanCacheFactory;
import b.currencycrawler.data.mongodb.MongoClientConfigurationException;
import b.currencycrawler.data.mongodb.MongoClientFactory;
import b.currencycrawler.model.CurrencyRate;
import b.currencycrawler.model.CurrencyRates;
import b.currencycrawler.services.CurrencyRatesService;
import com.mybaas.commons.proxyclasses.PaginationRequest;
import io.vertx.amqp.AmqpClient;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

import java.util.List;

public class CurrencyRateServiceImpl  implements CurrencyRatesService {

    private static final String DB_COLLECTION_NAME_RATES = "Rates";
    public static final String QUEUE_ADDRESS_CURRENCYRATES = "CurrencyRates-Announcing";


    private MongoClient mongoClient;
    private RemoteCacheManager cacheManager;
    private RemoteCache<String, CurrencyRates> cache;
    private AmqpClient amqpClient;

    private RemoteCache<String, Integer> cache2 ;


    public CurrencyRateServiceImpl(Vertx vertx) throws MongoClientConfigurationException, InfinispanCacheConfigurationException, AmqpClientConfigurationException {
        mongoClient = MongoClientFactory.buildClient(vertx);
        cacheManager = InfinispanCacheFactory.getCacheManager();
        cache = cacheManager.getCache(CurrencyRatesService.CACHE_NAME);
        cache2 = cacheManager.getCache(CurrencyRatesService.CACHE_NAME);

        amqpClient = AmqpClientFactory.getAmqpClient();
    }


    @Override
    public CurrencyRatesService getLatestCurrencyRates(Handler<AsyncResult<JsonObject>> resultHandler) {

        Promise<JsonObject> promise = Promise.promise();


        FindOptions findOptions = new FindOptions().setLimit(1).setSort(new JsonObject().put("_id", -1));
        mongoClient.findWithOptions("Rates", new JsonObject(), findOptions, res -> {
            if (res.succeeded()){
                if (res.result().size() == 0) {
                    promise.complete(new JsonObject());
                } else {
                    promise.complete(res.result().get(0));
                }
            }
            else {
                promise.fail(res.cause());
            }
        });
        promise.future().setHandler(r -> {
           resultHandler.handle(r);
        });
        return this;
    }

    @Override
    public CurrencyRatesService getCurrencyRates(PaginationRequest paginationOptions, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
        Promise<List<JsonObject>> promise = Promise.promise();
        FindOptions findOptions = new FindOptions()
                .setLimit(paginationOptions.getSize())
                .setSkip(paginationOptions.getPageNumber());

        mongoClient.findWithOptions("Rates", new JsonObject(), findOptions, res -> {
            if (res.succeeded()){
                promise.complete(res.result());
            }
            else {
                promise.fail(res.cause());
            }
        });
        promise.future().setHandler(r -> {
            resultHandler.handle(r);
        });
        return this;
    }


    public CurrencyRatesService writeAndCacheRatesToDb(CurrencyRates rates, Handler<AsyncResult<CurrencyRates>> resultHandler) {
        Future<CurrencyRates> future = writeRatesIntoDB(rates);
        future.compose(this::putCurrencyRatesIntoCache).setHandler(resultHandler);
        //future.setHandler(resultHandler);
        return this;
    }


    public Future<CurrencyRates> putCurrencyRatesIntoCache(CurrencyRates rates /*, Handler<AsyncResult<CurrencyRates>> resultHandler*/) {
        Promise<CurrencyRates> promise = Promise.promise();
        try {
            //final RedisClient client = RedisClient.create(vertx, new RedisOptions().setHost(host));
            cache.put(CurrencyRatesService.CACHE_KEY_LAST_CURRENCIES, rates);
            cache2.put("Key", 1000);

            int i = cache2.get("Key");
            promise.complete(rates);
        }catch (Exception e) {
            promise.fail(e);
        }
        return promise.future();
        //promise.future().setHandler(r-> resultHandler.handle(r));
    }

    public Future<CurrencyRates> writeRatesIntoDB(CurrencyRates rates/*, Handler<AsyncResult<CurrencyRates>> resultHandler*/) {
        Promise<CurrencyRates> promise = Promise.promise();
        try {
            JsonObject jsonObject = rates.toJson();
            mongoClient.insert("Rates", jsonObject, res -> {
                if (res.succeeded()) {
                    promise.complete(rates);
                } else if (res.failed()) {
                    promise.fail(res.cause());
                }
            });
        }catch (Exception e) {
            promise.fail(e);
        }
        return promise.future();
        //promise.future().setHandler(r-> resultHandler.handle(r));
    }
}
