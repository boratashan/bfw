package vertx.cores;

import io.vertx.core.Context;
import io.vertx.core.Vertx;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class DummyLoopThread {

    private Runnable runnable = new SubRunnable();
    private Thread thread;
    private boolean isStop;
    private boolean returnFunc;
    private ThreadLocalRandom random;
    private Context context;
    Consumer<Integer> consumer;


    public DummyLoopThread(Context context) {
        isStop = true;
        returnFunc = false;
        this.context = context;
        random = ThreadLocalRandom.current();
        this.thread = new Thread(this.runnable, "MyThread");
    }

    public static void main(String[] args) {
        DummyLoopThread d = new DummyLoopThread(null);
        d.start();

    }

    public void getRandomInt(Consumer<Integer> consumer) {
        this.consumer = consumer;
        returnFunc = true;
    }

    public void start() {
        this.isStop = false;
        thread.start();
    }

    public void stop() {
        this.isStop = true;
        thread.isInterrupted();
    }


    private class SubRunnable implements Runnable {
        @Override
        public void run() {
            System.out.println(String.format("Thread [%s] is starting", Thread.currentThread().getName()));
            try {
                while (!isStop) {
                    thread.sleep(5000);
                    System.out.println(String.format("Thread [%s] is running", Thread.currentThread().getName()));
                    if (returnFunc) {
                        returnFunc = false;
//                        context.runOnContext(event -> {

                            consumer.accept(random.nextInt(100));
  //                      });

                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
