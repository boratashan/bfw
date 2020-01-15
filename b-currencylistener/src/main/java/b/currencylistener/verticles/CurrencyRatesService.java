package b.currencylistener.verticles;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;


public interface CurrencyRatesService {

    String SERVICE_NAME = "Currency-Rates-EventBus-Service";
    String SERVICE_ADDRESS = "service.currencyratesservice";
    @Fluent
    CurrencyRatesService readLastCurrencyRates(Handler<AsyncResult<JsonArray>> resultHandler);
}
