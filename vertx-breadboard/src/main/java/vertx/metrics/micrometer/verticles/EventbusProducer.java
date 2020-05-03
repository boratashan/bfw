package vertx.metrics.micrometer.verticles;

import io.vertx.core.AbstractVerticle;

/**
 * @author Joel Takvorian, jtakvori@redhat.com
 */
public class EventbusProducer extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        vertx.setPeriodic(1000,
            x -> {
                Greetings.get(vertx, greetingResult -> vertx.eventBus().send("greeting", greetingResult.result()));
            });
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
