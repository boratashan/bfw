package b.contentcollector.targetsuppliers;

import b.contentcollector.model.ContentTarget;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class MongoDbTargetSupplier implements FutureSupplier<Optional<ContentTarget>> {
    private static final String COLLECTION_TARGETS = "content-targets";
    private final String sourceCollection;
    private final MongoClient mongoClient;
    private final ObjectMapper mapper;
    private AtomicInteger atomInt = new AtomicInteger(0);

    private MongoDbTargetSupplier(Builder builder) {
        mongoClient = builder.mongoClient;
        sourceCollection = builder.sourceCollection;
        mapper = builder.mapper;
    }

    private Integer getNextIndex() {
        return atomInt.getAndIncrement();
    }

    private void doGet(AtomicInteger atomInt, Handler<AsyncResult<Optional<ContentTarget>>> resultHandler) {
        FindOptions options = new FindOptions()
                .setLimit(1)
                .setSkip(atomInt.getAndIncrement());
        JsonObject query = new JsonObject().put("enabled", true);
        Promise<Optional<ContentTarget>> promise = Promise.promise();
        promise.future().setHandler(event -> resultHandler.handle(event));
        mongoClient.findWithOptions(sourceCollection, query, options, ar -> {
                    if (ar.succeeded()) {
                        if (ar.result().size() > 0) {
                            JsonObject res = ar.result().get(0);
                            ContentTarget contentTarget =  res.mapTo(ContentTarget.class);
                            promise.complete(Optional.of(contentTarget));
                        } else {
                            atomInt.set(0);
                            promise.complete(Optional.empty());
                            /*Recursive call is quite dangerous here as collection might be empty and brings code into infinite loop*/
                            /*doGet(atomInt, event -> {
                                promise.complete(event.result());
                            });*/
                        }
                    }
                    else if (ar.failed()) {
                        promise.fail(ar.cause());
                    }
                });
    }

    private Future<Optional<ContentTarget>> doGet(AtomicInteger atomInt) {
        Promise<Optional<ContentTarget>> promise = Promise.promise();
        this.doGet(atomInt, ar -> {
            if (ar.succeeded()) {
                promise.complete(ar.result());
            } else if (ar.failed()) {
                promise.fail(ar.cause());
            }
        });
        return promise.future();
    }

    @Override
    public Future<Optional<ContentTarget>> getNext() {
        return doGet(atomInt);
    }

    @Override
    public Future<List<Optional<ContentTarget>>> getAll() {
        JsonObject query = new JsonObject()
                .put("enabled", true);
        Promise<List<Optional<ContentTarget>>> promise = Promise.promise();
        mongoClient.find(sourceCollection, query, ar -> {
            if (ar.succeeded()) {
                List<Optional<ContentTarget>> targets = new ArrayList<>();
                for(JsonObject j : ar.result()) {
                    ContentTarget contentTarget = j.mapTo(ContentTarget.class);
                    targets.add(Optional.of(contentTarget));
                }
                promise.complete(targets);
            }
            else if (ar.failed()) {
                promise.fail(ar.cause());
            }
        });
        return promise.future();
    }


    public static final class Builder {
        private String sourceCollection;
        private MongoClient mongoClient;
        private ObjectMapper mapper;

        public Builder() {
        }

        public Builder withSourceCollection(String val) {
            sourceCollection = val;
            return this;
        }

        public Builder withMongoClient(MongoClient val) {
            mongoClient = val;
            return this;
        }

        public Builder withMapper(ObjectMapper val) {
            mapper = val;
            return this;
        }

        public MongoDbTargetSupplier build() {
            return new MongoDbTargetSupplier(this);
        }
    }
}
