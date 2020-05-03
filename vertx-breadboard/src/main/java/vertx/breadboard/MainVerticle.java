package vertx.breadboard;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Launcher;
import io.vertx.core.Vertx;

/**
 * An example illustrating how worker verticles can be deployed and how to interact with them.
 *
 * This example prints the name of the current thread at various locations to exhibit the event loop <-> worker
 * thread switches.
 */
public class MainVerticle extends AbstractVerticle {

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {
        System.out.println(Runtime.version().toString());
        Runner.runExample(MainVerticle.class);
    }

    @Override
    public void start() throws Exception {
        System.out.println("[Main] Running in " + Thread.currentThread().getName());
        vertx.deployVerticle(WorkerVerticle.class, new DeploymentOptions().setWorker(true));
        vertx.eventBus()
             .send("sample.data",
                   "hello vert.x",
                       r -> {
                           System.out.println("[Main] Receiving reply ' " + r.result().body() + "' in "
                                              + Thread.currentThread().getName());
                       });
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Stopping...");
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
