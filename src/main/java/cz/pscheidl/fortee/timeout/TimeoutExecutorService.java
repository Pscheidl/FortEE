package cz.pscheidl.fortee.timeout;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Pavel Pscheidl
 */
public class TimeoutExecutorService implements ExecutorService {

    private ThreadTimer threadTimer;
    private int timeout;

    public TimeoutExecutorService(ExecutorService delegate, int timeout) {
        this.delegate = delegate;
        this.timeout = timeout;
        threadTimer = new ThreadTimer(timeout);
    }

    private ExecutorService delegate;

    @Override
    public void shutdown() {
        delegate.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return delegate.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        Future<T> future = delegate.submit(task);
        threadTimer.watchForTimeout(future);
        return future;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result)
    {
        Future<T> future = delegate.submit(task, result);
        threadTimer.watchForTimeout( future);
        return future;
    }

    @Override
    public Future<?> submit(Runnable task) {
        Future<?> future = delegate.submit(task);
        threadTimer.watchForTimeout(future);
        return future;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return delegate.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return delegate.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        delegate.execute(command);
    }
}
