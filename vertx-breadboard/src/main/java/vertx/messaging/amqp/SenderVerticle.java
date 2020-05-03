package vertx.messaging.amqp;

import io.vertx.amqp.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import vertx.breadboard.Runner;

public class SenderVerticle extends AbstractVerticle {
    private AmqpClient amqpClient;
    private AmqpConnection amqpConnection;
    private AmqpSender amqpSender;

    public static void main(String[] args) {
        System.out.println(Runtime.version().toString());
        Runner.runExample(SenderVerticle.class);
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        System.out.println("[Main] Running in " + Thread.currentThread().getName());

//        vertx.deployVerticle(ReceiverVerticle.class, new DeploymentOptions());

        setupAmqpClient().onComplete(event -> {
            if (event.succeeded()) {
                vertx.setPeriodic(
                        100,
                        timer -> {
                            String msg = "hello";
                            AmqpMessageBuilder builder = AmqpMessageBuilder.create();
                            AmqpMessage message = builder.withBody(msg).durable(true).build();
                            System.out.println(String.format("SENDER ------- [%s]", message.bodyAsString()));

                            amqpSender.sendWithAck(message, ack -> {
                                if (ack.succeeded()) {
                                    System.out.println(String.format("SENDER (Ack Received)) [%s]", message.bodyAsString()));
                                }
                                else {
                                    System.out.println(String.format("SENDER (Ack Rejected)) [%s]", message.bodyAsString()));
                                }
                            });
                            //amqpSender.send(message);
                        });
                startPromise.complete();
            } else {
                startPromise.fail(event.cause());
                vertx.close();
            }
        });
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        stopPromise.complete();
    }

    private Future<Void> setupAmqpClient() {
        Promise<Void> promise = Promise.promise();
        AmqpClientOptions amqpClientOptions =
                new AmqpClientOptions()
                        .setHost("127.0.0.1")
                        .setPort(5672)
                        .setUsername("client")
                        .setPassword("client");

        amqpClient = AmqpClient.create(vertx, amqpClientOptions);
        amqpClient.connect(
                ar -> {
                    if (ar.failed()) {
                        System.out.println("Unable to connect to the broker");
                        promise.fail(ar.cause());
                    } else {
                        System.out.println("Connection succeeded");
                        amqpConnection = ar.result();

                        amqpConnection.createSender(
                                "queue.test",
                                event1 -> {
                                    if (event1.succeeded()) {
                                        amqpSender = event1.result();

                                        promise.complete();
                                    } else {
                                        promise.fail(event1.cause());
                                    }
                                });
                    }
                });

        return promise.future();
    }
}
