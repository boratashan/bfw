package b.currencycrawler.api;

import com.mybaas.commons.proxyclasses.PaginationRequest;
import com.mybaas.commons.model.QueryStringParsingException;
import b.currencycrawler.services.CurrencyRatesService;
import com.mybaas.commons.BaseRestAPIVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class CrawlerApiVerticle extends BaseRestAPIVerticle {
    public static final String SERVICE_NAME = "crawler-rest-api";
    private static final String API_RETRIEVE = "/crawler";
    private static final String API_RETRIEVE_THE_LAST = "/crawler/last";
    private static final String API_RETRIEVE_ALL = "/crawler/rates";

    private CurrencyRatesService currencyRatesService;


    public CrawlerApiVerticle(CurrencyRatesService currencyRatesService) {
        this.currencyRatesService = currencyRatesService;
    }

    public void start(Future<Void> future) throws Exception {
        super.start();
        final Router router = Router.router(vertx);
        this.enableBodyHandler(router);


        router.get(API_RETRIEVE).handler(this::apiRetrieve);
        router.get(API_RETRIEVE_THE_LAST).handler(this::apiRetrieveTheLast);
        router.get(API_RETRIEVE_ALL).handler(this::apiRetrieveAll);
        // get HTTP host and port from configuration, or use default value
        String host = config().getString("docstore.http.address", "0.0.0.0");
        int port = config().getInteger("docstore.http.port", 8082);

        // create HTTP server and publish REST service
        createHttpServer(router, host, port)
                .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port))
                .setHandler(future).map(r -> null);
    }

    //FIXME  Add seperate exception logger and link Fatal error and the Exception with Uniquie ID
    //  --Do not return back internal exceptions to api consumers, Provide simple message together with UniqueID to find further exception details.e
    private void apiRetrieve(RoutingContext context) {
        context.response().setStatusCode(201).putHeader("content-type", "application/json")
                .end("Crawler Retrieve DONE");
    }

    private void apiRetrieveTheLast(RoutingContext context) {

       currencyRatesService.getLatestCurrencyRates(resultHandler(context,
               currencyRates -> {
                    return currencyRates.encodePrettily();
                })
       );
    }


    private void apiRetrieveAll(RoutingContext context) throws  QueryStringParsingException {
        PaginationRequest paginationOptions = this.extractPaginationRequest(context);
        currencyRatesService.getCurrencyRates(paginationOptions, resultHandler(context, result -> {
            JsonArray jsonArray = new JsonArray(result);
            return jsonArray.encodePrettily();
        }));
    }




}
