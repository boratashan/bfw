package b.currencylistener.verticles;

import com.mybaas.commons.BaseVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class FutureSampleVerticle extends BaseVerticle {

    private static final Logger logger = LogManager.getLogger(FutureSampleVerticle.class);
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        System.out.println("Welcome to Vertx");
        startPromise.complete();
    }


    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        System.out.println("Bye bye from Vertx");
        stopPromise.complete();
    }
}
