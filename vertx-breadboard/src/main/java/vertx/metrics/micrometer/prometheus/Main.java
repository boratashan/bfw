package vertx.metrics.micrometer.prometheus;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxInfluxDbOptions;
import io.vertx.micrometer.VertxPrometheusOptions;

import vertx.metrics.micrometer.verticles.EventbusConsumer;
import vertx.metrics.micrometer.verticles.EventbusProducer;
import vertx.metrics.micrometer.verticles.SimpleWebServer;

public class Main {
    public static void main(String[] args) {

        // Default InfluxDB options will push metrics to localhost:8086, db "default"

        /*
         * MicrometerMetricsOptions options = new MicrometerMetricsOptions()
         *       .setPrometheusOptions(new VertxPrometheusOptions()
         *               .setStartEmbeddedServer(true)
         *               .setEmbeddedServerOptions(new HttpServerOptions().setPort(9021))
         *               .setEmbeddedServerEndpoint("/metrics/vertx")
         *               .setEnabled(true))
         *       .setEnabled(true);
         */

        // Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(options));
        Vertx vertx = Vertx.vertx(
                          new VertxOptions().setMetricsOptions(
                              new MicrometerMetricsOptions().setPrometheusOptions(
                                  new VertxPrometheusOptions().setEnabled(true)
                                                              .setStartEmbeddedServer(true)
                                                              .setEmbeddedServerOptions(
                                                                  new HttpServerOptions().setPort(8088))

        // .setEmbeddedServerEndpoint("/metrics/vertx")
        )
                                                            .setEnabled(true)));

        vertx.deployVerticle(new SimpleWebServer());
        vertx.deployVerticle(new EventbusConsumer());
        vertx.deployVerticle(new EventbusProducer());
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
