package b.currencylistener.verticles;

import com.mybaas.commons.BaseVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SampleVerticle extends BaseVerticle {

    private static final Logger logger = LogManager.getLogger(SampleVerticle.class);
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        System.out.println("Welcome to Vertx");
        String options = this.config().toString();
        logger.debug(String.format("Deployment options : %s", options));
        logger.info("Deploying verticles...");
        DeploymentOptions deploymentOptions = new DeploymentOptions(new JsonObject(options));
        vertx.deployVerticle(CurrencyListenerVerticle::new,  deploymentOptions);
        logger.info("Deploying verticles [DONE]");
        startPromise.complete();
    }


    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        System.out.println("Bye bye from Vertx");
        stopPromise.complete();
    }
}
