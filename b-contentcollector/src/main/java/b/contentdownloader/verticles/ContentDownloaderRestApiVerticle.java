package b.contentdownloader.verticles;

import b.QueryStringParsingException;
import b.contentdownloader.service.ContentDownloaderService;
import com.mybaas.commons.BaseRestAPIVerticle;
import com.mybaas.commons.mongodb.MongoClientFactory;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;

public class ContentDownloaderRestApiVerticle extends BaseRestAPIVerticle {

    private static final String API_ROOT = "/api";
    private static final String API_GET_NUMBER_OF_DOWNLOADED_CONTENT = "/numbers-of-contents";
    private static final String API_GET_LIST_OF_CONTENTS = "/contents";
    private static final String API_GET_CONTENT = "/contents/:id";
    private static final String API_DELETE_CONTENT = "/contents/:id";

    private Router router;
    private MongoClient mongoClient;
    private ContentDownloaderService downloaderService;


    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        mongoClient = MongoClientFactory.buildClient(vertx);
        downloaderService = ContentDownloaderService.create(mongoClient);
        this.router = Router.router(vertx);
        router.get(API_GET_NUMBER_OF_DOWNLOADED_CONTENT).handler(this::apiGetNumberOfDownloadedContent);
        router.get(API_GET_LIST_OF_CONTENTS).handler(this::apiGetListOfContentsIDs);
        router.get(API_GET_CONTENT).handler(this::apiGetContent);
        router.delete(API_DELETE_CONTENT).handler(this::apiDeleteContent);
        HttpServer server = vertx.createHttpServer();
        server.
                requestHandler(router)
                .listen(9300);
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {

    }


    private void apiGetNumberOfDownloadedContent(RoutingContext context) {
        downloaderService.getNumberOfDownloadedContent(event -> {
            JsonObject resp = new JsonObject();
            if (event.succeeded()) {
                resp.put("count", event.result());
                context.response().setStatusCode(200);
                context.response().putHeader("Content-Type", "application/json");
                context.response().end(resp.encode());
            }
            else {
                resp.put("error", event.cause().getMessage());
                context.response().setStatusCode(500);
                context.response().putHeader("Content-Type", "application/json");
                context.response().end(resp.encode());
            }
        });
    }

    private static Map<String, String> queryStringToMap(RoutingContext context) throws UnsupportedEncodingException {
        String query = URLDecoder.decode(StringUtils.defaultIfEmpty(context.request().query(), "") , "UTF-8");
        return queryStringToMap(query);
    }

    private static Map<String, String> queryStringToMap(String queryString) throws IllegalFormatException {
        try {
            Map<String, String> result = new HashMap<>();
            Arrays.stream(queryString.split("&"))
                    .forEach(s -> {
                        String[] kv = s.split("=");
                        result.put(kv[0], kv.length > 1 ? kv[1] : null);
                    });
            return result;
        }catch (Exception e) {
            throw new QueryStringParsingException(String.format("Query string parsing exception. Query -> [%s]", queryString), e);
        }
    }

    private void apiGetListOfContentsIDs(RoutingContext context) {
        Map<String, String> query = queryStringToMap(context.request().query());
        int limit = Integer.valueOf(query.get("limit"));
        int offset = Integer.valueOf(query.get("offset"));
        downloaderService.getListOfContentIDs(limit, offset, event -> {
            JsonObject resp = new JsonObject();
            if (event.succeeded()) {
                JsonArray arr = new JsonArray(event.result());
                resp.put("data", arr);
                context.response().setStatusCode(200);
                context.response().putHeader("Content-Type", "application/json");
                context.response().end(resp.encode());
            }
            else {
                resp.put("error", event.cause().getMessage());
                context.response().setStatusCode(500);
                context.response().putHeader("Content-Type", "application/json");
                context.response().end(resp.encode());
            }
        });
    }

    private void apiGetContent(RoutingContext context) {
        String contentID = context.request().params().get("id");
        downloaderService.getContentData(contentID, event -> {
            JsonObject resp = new JsonObject();
            if (event.succeeded()) {
                String s = event.result().getJsonObject("content").getString("$binary");
                resp.put("content", s);
                context.response().setStatusCode(200);
                context.response().putHeader("Content-Type", "application/json");
                context.response().end(event.result().encodePrettily());
            }
            else {
                resp.put("error", event.cause().getMessage());
                context.response().setStatusCode(500);
                context.response().putHeader("Content-Type", "application/json");
                context.response().end(resp.encode());
            }
        });
    }

    private void apiDeleteContent(RoutingContext context) {
        String contentID = context.request().params().get("id");
        downloaderService.deleteContentData(contentID, event -> {
            if (event.succeeded()) {
                context.response().setStatusCode(200);
                context.response().putHeader("Content-Type", "application/json");
                context.response().end(new JsonObject().put("success", true).encode());
            } else {
                context.response().setStatusCode(500);
                context.response().putHeader("Content-Type", "application/json");
                context.response().end(new JsonObject()
                        .put("success", false)
                        .put("error", event.cause().getMessage()).encode());
            }
        });
    }
}
