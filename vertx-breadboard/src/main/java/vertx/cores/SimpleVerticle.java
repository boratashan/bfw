package vertx.cores;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class SimpleVerticle extends AbstractVerticle {
    private Long timerID;
    private DummyLoopThread dummyLoopThread;




    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        Context context = vertx.getOrCreateContext();
        DummyLoopThread dummyLoopThread = new DummyLoopThread(context);
        dummyLoopThread.start();
        vertx.deployVerticle(new SimpleVerticle(dummyLoopThread));

    }


    public SimpleVerticle(DummyLoopThread dummyLoopThread) {
        this.dummyLoopThread = dummyLoopThread;
    }

    @Override

    public void start(Promise<Void> startPromise) throws Exception {

        dummyLoopThread.getRandomInt(integer -> {
            System.out.println(String.format("Thread [%s] has returned %d", Thread.currentThread().getName(), integer));
            dummyLoopThread.getRandomInt(integer1 -> String.format("Thread [%s] has returned %d", Thread.currentThread().getName(), integer1));
        });

        timerID = vertx.setPeriodic(1000, event -> {
            System.out.println(String.format("Hello [%s] from vert.x", Thread.currentThread().getName()));
        });

        startPromise.complete();
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        vertx.cancelTimer(timerID);
        stopPromise.complete();
    }


}
