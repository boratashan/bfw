package vertx.metrics.micrometer.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.micrometer.PrometheusScrapingHandler;

/**
 * @author Joel Takvorian, jtakvori@redhat.com
 */
public class SimpleWebServer extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);

        router.get("/").handler(ctx -> {
                Greetings.get(vertx, greetingResult -> ctx.response().end(greetingResult.result()));
            });
        router.route("/metrics").handler(PrometheusScrapingHandler.create());
        vertx.createHttpServer().requestHandler(router).listen(8080);
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
