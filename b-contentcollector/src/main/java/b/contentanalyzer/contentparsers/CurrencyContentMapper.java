package b.contentanalyzer.contentparsers;

import b.contentanalyzer.model.Currency;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Optional;

public class CurrencyContentMapper  {

    private MongoClient mongoClient;
    private List<JsonObject> mapList;



    public CurrencyContentMapper(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public Future<Optional<Pair<String, String>>> getBuyAndSellSelector(String project, String target, Currency currency) {
        Promise<Optional<Pair<String, String>>> promise = Promise.promise();
        //{$and:[{"target":"uzmanpara"}, {"project":"currencycollect"}]}
        JsonObject query = new JsonObject()
                .put("$and", new JsonArray()
                                .add(new JsonObject().put("project", project))
                                .add(new JsonObject().put("target", target))
                );
        mongoClient.findOne("content-crawler-mapping", query, new JsonObject(), event ->  {
            if (event.succeeded()) {
                JsonObject jsonObject = event.result();
                JsonArray array =  jsonObject.getJsonArray("conf");
                Optional<Pair<String, String>> pair = Optional.empty();
                for (int i = 0; i< array.size(); i++) {
                    JsonObject object = array.getJsonObject(i);
                    if (object.getString("currcode").equalsIgnoreCase(currency.toString())) {
                        String buy  = object.getString("buy");
                        String sell = object.getString("sell");
                        pair = Optional.of(Pair.of(buy,sell));
                    }
                }
                promise.complete(pair);
            }
            else {
                promise.fail(event.cause());
            }
        });
        return promise.future();
    }


}
