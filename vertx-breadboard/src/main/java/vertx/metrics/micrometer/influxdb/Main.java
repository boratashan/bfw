package vertx.metrics.micrometer.influxdb;

import io.micrometer.core.instrument.MeterRegistry;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxInfluxDbOptions;

import vertx.metrics.micrometer.verticles.EventbusConsumer;
import vertx.metrics.micrometer.verticles.EventbusProducer;
import vertx.metrics.micrometer.verticles.SimpleWebServer;

public class Main {
    public static void main(String[] args) {

        // Default InfluxDB options will push metrics to localhost:8086, db "default"
        MicrometerMetricsOptions options =
            new MicrometerMetricsOptions().setInfluxDbOptions(new VertxInfluxDbOptions().setEnabled(true)
                                                                                        .setUri("http://localhost:9999")
                                                                                        .setDb("BucketFirst")
                                                                                        .setUserName("root")
                                                                                        .setPassword("root1234"))
                                          .setEnabled(true);
        Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(options));

        vertx.deployVerticle(new SimpleWebServer());
        vertx.deployVerticle(new EventbusConsumer());
        vertx.deployVerticle(new EventbusProducer());
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
