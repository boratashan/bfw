package b.currencylistener.verticles;

import com.mybaas.commons.BaseMicroServiceVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CurrencyListenerVerticle extends BaseMicroServiceVerticle {
    public static final String EVB_ADDRESS_CURRENCYRATES = "CurrencyRates-Announcing";

    @Override
    public void start() throws Exception {
        vertx.eventBus().<JsonArray>consumer(EVB_ADDRESS_CURRENCYRATES, message -> {
            // Populate the `quotes` map with the received quote
            // Quotes are json objects you can retrieve from the message body
            // The map is structured as follows: name -> quote

            // ----
            JsonArray array = message.body();
            System.out.println(array.toString());

            CurrencyRatesServiceVertxEBProxy proxy = new CurrencyRatesServiceVertxEBProxy(vertx, "service.currencyratesservice");
            proxy.readLastCurrencyRates(res -> {
                if (res.succeeded()) {
                    System.out.println(res.result().toString());
                }
                else if (res.failed()) {
                    System.out.println(res.cause().getMessage());
                }
            });


            // ----
            vertx.sharedData().getCounter("Crawler_Counter", r -> {
                if (r.succeeded()) {
                    r.result().get( res -> {
                        if (res.succeeded()) {
                            Long result = res.result();
                            System.out.print(String.format("Shared data listener : %s", result.toString()));
                        }
                    });
                }
            }) ;

        });
    }
}
