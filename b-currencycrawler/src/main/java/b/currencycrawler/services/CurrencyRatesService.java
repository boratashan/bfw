package b.currencycrawler.services;

import b.currencycrawler.model.CurrencyRate;
import b.currencycrawler.model.CurrencyRates;
import com.mybaas.commons.proxyclasses.PaginationRequest;

import io.vertx.codegen.annotations.*;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.util.List;

//@VertxGen
@ProxyGen
public interface CurrencyRatesService {

    public static final String CACHE_NAME = "Cache.CurrencyRatesService";
    public static final String CACHE_KEY_LAST_CURRENCIES = "Cache.CurrencyRatesService";

    String SERVICE_NAME = "Currency-Rates-EventBus-Service";
    String SERVICE_ADDRESS = "service.currencyratesservice";
    @Fluent
    CurrencyRatesService getLatestCurrencyRates(Handler<AsyncResult<JsonObject>> resultHandler);

    @Fluent
    CurrencyRatesService getCurrencyRates(PaginationRequest paginationOptions, Handler<AsyncResult<List<JsonObject>>> resultHandler);

    @ProxyIgnore
    @Fluent
    CurrencyRatesService writeAndCacheRatesToDb(CurrencyRates rates, Handler<AsyncResult<CurrencyRates>> resultHandler);

}
