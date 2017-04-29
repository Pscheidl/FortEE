package cz.pscheidl.fortee.timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pavel Pscheidl
 */
@RunWith(Arquillian.class)
public class TimeoutPoolTest {

    @Inject
    @Timeout(millis = 10, threads = 1000)
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
    public void testManyCancelled() throws InterruptedException, ExecutionException {
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

        Thread.sleep(11);

        long doneFutures = submittedTasks.stream()
                .filter(Future::isDone)
                .count();

        long cancelledFutures = submittedTasks.stream()
                .filter(Future::isCancelled)
                .count();

        Assert.assertEquals(1000, doneFutures);
        Assert.assertEquals(0, cancelledFutures);
    }

    @Test(expected = CancellationException.class)
    public void testInvokeAllTimeout() throws InterruptedException, ExecutionException {
        List<Callable<Optional<String>>> callables = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            callables.add(() -> {
                Thread.sleep(11);
                return Optional.empty();
            });
        }
        List<Future<Optional<String>>> futures = executorService.invokeAll(callables, 10, TimeUnit.MILLISECONDS);

        Assert.assertEquals(callables.size(), futures.size());

        long cancelledCount = futures.stream()
                .filter(Future::isCancelled)
                .count();
        Assert.assertEquals(callables.size(), cancelledCount);
        futures.get(0).get();
    }

    @Test
    public void testInvokeAll() throws InterruptedException {
        List<Callable<Optional<String>>> callables = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            callables.add(() -> {
                return Optional.empty();
            });
        }
        List<Future<Optional<String>>> futures = executorService.invokeAll(callables);

        Assert.assertEquals(callables.size(), futures.size());

        long cancelledCount = futures.stream()
                .filter(Future::isCancelled)
                .count();
        Assert.assertEquals(0, cancelledCount);
    }

    @Test
    public void testInvokeAny() throws InterruptedException, ExecutionException {
        List<Callable<Optional<String>>> callables = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            callables.add(() -> {
                return Optional.empty();
            });
        }
        Optional<String> s = executorService.invokeAny(callables);

        Thread.sleep(11);
        Assert.assertNotNull(s);
        Assert.assertFalse(s.isPresent());
    }

    @Test(expected = ExecutionException.class)
    public void testInvokeAnyException() throws ExecutionException, InterruptedException {
        List<Callable<Optional<String>>> callables = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            callables.add(() -> {
                throw new RuntimeException();
            });
        }
        Optional<String> s = executorService.invokeAny(callables);
    }
}
