package b.downloadclock;

import b.commons.model.ContentTarget;
import b.messagecodecs.ContentTargetMessageCodec;
import com.mybaas.commons.BaseMicroServiceVerticle;
import com.mybaas.commons.mongodb.MongoClientFactory;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.Lock;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.Objects;


public class DownloadRequestEmitterVerticle extends BaseMicroServiceVerticle {

    private static final String SOURCE_COLLECTION = "content-targets";
    private static final String LOCK_COLLECTION = "lock.content-targets";
    private static final String MAP_COLLECTION = "map.content-targets.state";
    private static final String EB_DOWNLOAD_REQUEST = "download.request";

    private static final String MAP_SHARED = "download.emitter.lastid";



    private static final int DEFAULT_BLOCK_COUNT = 2;
    private static final long TIMER_INTERVAL = 1000*5;
    private static final long TIMER_INTERVAL_AFTER_ERROR = TIMER_INTERVAL*10;



    private MongoClient mongoClient;
    private ContentTargetMessageCodec codec;
    private SharedData sharedData;
    private AsyncMap<String, String> sharedMap;
    private Long timerID;


    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        getLogger().info("################################################################################");
        getLogger().info("Starting DownloadRequestEmitter");
        codec = new ContentTargetMessageCodec();
        vertx.eventBus().registerCodec(codec);

        mongoClient = MongoClientFactory.buildClient(vertx);
        sharedData =  vertx.sharedData();
        sharedData.<String, String>getLocalAsyncMap(MAP_COLLECTION, ar -> {
            if (ar.succeeded()) {
                sharedMap =  ar.result();
                getLogger().debug("Resetting timer");
                timerID = vertx.setTimer(TIMER_INTERVAL, event -> {
                    startEmitting();
                });
                startPromise.complete();
                getLogger().info("Starting DownloadRequestEmitter[DONE]");
            }
            else {
                startPromise.fail(ar.cause());
            }
        });
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        getLogger().info("Stopping DownloadRequestEmitter");
        vertx.cancelTimer(timerID);
        getLogger().info("Starting DownloadRequestEmitter[DONE]");
    }

    public void startEmitting() {
        getLogger().debug("Start Emitting...");
        acquireLock().compose(this::readAndEmitSet).setHandler(event -> {
            long nextInterval = TIMER_INTERVAL;
            if (event.succeeded()) {
                getLogger().debug("Start Emitting is DONE");
            }
            else {
                nextInterval = TIMER_INTERVAL_AFTER_ERROR;
                getLogger().error(String.format("Start Emitting has error %s", event.cause()));
            }
            getLogger().debug(String.format("Resetting timer, next timer is set to the next %d ms.", nextInterval));
            vertx.setTimer(nextInterval, ar -> {
                startEmitting();
            });
        });
    }



    private Future<Lock> acquireLock() {
        getLogger().debug("Acquiring a lock");
        Promise<Lock> promise = Promise.promise();
        sharedData.getLocalLock(LOCK_COLLECTION, promise);
        return promise.future();
    }


    public Future<Void> readAndEmitSet(Lock lock) {
        Promise<Void> promise = Promise.promise();
        FindOptions options = new FindOptions()
                .setLimit(DEFAULT_BLOCK_COUNT);

        sharedMap.get(MAP_SHARED, ar -> {
            if (ar.succeeded()) {
                String last = ar.result();
                getLogger().debug(String.format("Reading the last, %s ", last));
                JsonObject query = new JsonObject().put("enabled", true);
                if (!Strings.isBlank(last)) {
                    query.put("_id", new JsonObject().put("$gt",new JsonObject().put("$oid", last)));
                }
                mongoClient.findWithOptions(SOURCE_COLLECTION, query, options, event -> {
                   if (event.succeeded()) {
                        List<JsonObject> result =  event.result();
                       String updatedLast = null;
                        try {
                            for (JsonObject r : result) {
                                try {
                                    ContentTarget c = r.mapTo(ContentTarget.class);
                                    getLogger().debug(String.format("Sending eventbus message to addr:msg [%s][%s]", EB_DOWNLOAD_REQUEST, c.toString()));

                                    vertx.eventBus().send(EB_DOWNLOAD_REQUEST, c, new DeliveryOptions().setCodecName(codec.name()));
                                    updatedLast = c.getId();
                                } catch (IllegalArgumentException e) {
                                    promise.fail(e);
                                }
                            }
                        }
                        finally {
                            getLogger().debug(String.format("Setting the last, %s ", updatedLast));
                            sharedMap.put(MAP_SHARED, Objects.isNull(updatedLast)?Strings.EMPTY:updatedLast,res -> {
                                lock.release();
                                if (res.succeeded()) {
                                    promise.complete();
                                }
                                else {
                                    promise.fail(res.cause());
                                }
                            });
                        }
                   }
                   else {
                       promise.fail(event.cause());
                   }
                });
            } else {
                promise.fail(ar.cause());
            }
        });
        return promise.future();
    }
}
