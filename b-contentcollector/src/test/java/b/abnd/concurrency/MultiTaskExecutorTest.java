package b.abnd.concurrency;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class MultiTaskExecutorTest {
    private MultiTaskExecutor executor;

    @Before
    public void setUp() throws Exception {
        executor = new MultiTaskExecutor();
    }

    @Ignore
    @Test
    public void testExecutor() throws InterruptedException {
            executor.submitAndRunTasks((Callable<String>) () -> {
                System.out.printf("threadID %d , Message %s \n", Thread.currentThread().getId(), "Hello...");
                Thread.sleep(1000*5);
                return "Done";
            }, 1);
        executor.shutDown();
        executor.submitAndRunTasks((Callable<String>) () -> {
            System.out.printf("threadID %d , Message %s \n", Thread.currentThread().getId(), "Hello...");
            Thread.sleep(1000*5);
            return "Done";
        }, 1);

            List<Future<String>> results =  executor.readResults();

            while(!executor.isAllDone()) {
               System.out.printf("Waiting all is done...... \n");
               Thread.sleep(1000);

            }
        System.out.printf("\n Waiting all is done\n");
    }

    @Test
    public void testExecutorSimple() throws InterruptedException {
         MultiTaskExecutor<Boolean> executor = new MultiTaskExecutor<>();
         System.out.printf("\n Waiting all is done\n");
    }


    @After
    public void tearDown() throws Exception {
    }
}