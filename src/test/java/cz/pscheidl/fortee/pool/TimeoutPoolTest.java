package cz.pscheidl.fortee.pool;

import cz.pscheidl.fortee.timeout.Timeout;
import cz.pscheidl.fortee.timeout.TimeoutExecutorService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author Pavel Pscheidl
 */
@RunWith(Arquillian.class)
public class TimeoutPoolTest {

    @Inject
    @Timeout(millis = 10)
    private ExecutorService executorService;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, "cz.pscheidl.fortee")
                .addAsWebInfResource("beans.xml");
    }


    @Test
    public void textExecutorServiceInjection() {
        Assert.assertNotNull(executorService);
        Assert.assertTrue(executorService instanceof TimeoutExecutorService);


    }

    @Test
    public void testThreadCancellation() throws InterruptedException {
        Future<?> submit = executorService.submit(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
            }
        });

        Thread.sleep(11);
        Assert.assertTrue(submit.isCancelled());
    }

    public void testThreadNotCancelled() throws InterruptedException {
        Future<?> submit = executorService.submit(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
            }
        });

        Thread.sleep(9);
        Assert.assertFalse(submit.isCancelled());
    }

    @Test
    public void testManyCancelled() throws InterruptedException {
        List<Future> submittedTasks = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Future<?> submit = executorService.submit(() -> {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    return;
                }
            });
            submittedTasks.add(submit);
        }

        Thread.sleep(11);

        long count = submittedTasks.stream()
                .filter(Future::isCancelled)
                .count();

        Assert.assertEquals(1000, count);
    }

    @Test
    public void testManyNotCancelled() throws InterruptedException {
        List<Future> submittedTasks = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Future<?> submit = executorService.submit(() -> {

            });
            submittedTasks.add(submit);
        }

        Thread.sleep(10);

        long count = submittedTasks.stream()
                .filter(Future::isDone)
                .count();

        long cancelled = submittedTasks.stream()
                .filter(Future::isCancelled)
                .count();

        Assert.assertEquals(1000, count);
        Assert.assertEquals(0, cancelled);
    }
}
