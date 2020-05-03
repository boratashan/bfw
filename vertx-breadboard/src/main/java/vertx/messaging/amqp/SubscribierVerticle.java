package vertx.messaging.amqp;

import io.vertx.amqp.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import vertx.breadboard.Runner;

public class SubscribierVerticle extends AbstractVerticle {
  private AmqpClient amqpClient;
  private AmqpConnection amqpConnection;
  private AmqpReceiver amqpReceiver;

  public static void main(String[] args) {
    System.out.println(Runtime.version().toString());
    Runner.runExample(SubscribierVerticle.class);
  }

  @Override
  public void start() throws Exception {
    System.out.println("[Main] Running in " + Thread.currentThread().getName());

    setupAmqpClient();

  }

  @Override
  public void stop() throws Exception {
    System.out.println("Stopping...");
  }

  private Future<Void> setupAmqpClient() {
    Promise<Void> promise = Promise.promise();
    AmqpClientOptions amqpClientOptions =
        new AmqpClientOptions()
            .setHost("127.0.0.1")
            .setPort(5672)
            .setUsername("admin")
            .setPassword("admin");

    amqpClient = AmqpClient.create(vertx, amqpClientOptions);

    amqpClient.connect(
        ar -> {
          if (ar.failed()) {
            System.out.println("Unable to connect to the broker");
            promise.fail(ar.cause());
          } else {
            System.out.println("Connection succeeded");
            amqpConnection = ar.result();
            amqpConnection.createReceiver(
                "topic.test",
                new AmqpReceiverOptions().setAutoAcknowledgement(false),
                event1 -> {
                  if (event1.succeeded()) {
                    amqpReceiver = event1.result();

                    amqpReceiver.handler(this::messageHandler);
                    promise.complete();
                  } else {
                    promise.fail(event1.cause());
                  }
                });
          }
        });

    return promise.future();
  }

    private void messageHandler(AmqpMessage amqpMessage) {
      System.out.println(String.format("        RECEIVER ------- [%s]", amqpMessage.bodyAsString()));
      amqpMessage.accepted();

    }
}
