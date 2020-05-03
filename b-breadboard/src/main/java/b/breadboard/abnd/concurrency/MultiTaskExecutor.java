package b.breadboard.abnd.concurrency;

import org.checkerframework.checker.index.qual.Positive;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SuppressWarnings("initialization.fields.uninitialized")
public class MultiTaskExecutor<T> {

    public final static int DEFAULT_THREAD_POOL_SIZE = 4;
    private int threadPoolSize;
    private ExecutorService executorService;
    private List<Future<T>> futureList;

    public MultiTaskExecutor() {
        this(DEFAULT_THREAD_POOL_SIZE);
    }

    public MultiTaskExecutor(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
        this.executorService = Executors.newFixedThreadPool(this.threadPoolSize);
        futureList = new ArrayList<>();
    }

    public boolean submitAndRunTasks(Callable<T> task, @Positive int numberOfTasks)  {
        boolean res = true;
        try {
            for (int i = 0; i < numberOfTasks; i++) {
                futureList.add(executorService.submit(task));
            }
        }
        catch (RejectedExecutionException e){
            res = false;
        }
        return res;
    }

    public boolean isAllDone() {
        boolean res = false;
        res  = futureList.stream().map(Future::isDone).reduce(true, (a, b) -> a && b);
        return res;
    }

    public List<Future<T>> readResults() {
        return futureList;
    }

    public void shutDown() {
        if (!executorService.isShutdown())
            executorService.shutdown();
    }

    public boolean isTerminated() {
        return executorService.isTerminated();
    }

    public void waitForTermination(long timeoutInMilliSeconds) throws InterruptedException {
        executorService.awaitTermination(timeoutInMilliSeconds, TimeUnit.NANOSECONDS);
    }


}
