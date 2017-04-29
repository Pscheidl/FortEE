package com.github.pscheidl.fortee.timeout;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Pavel Pscheidl
 */
public class TimeoutExecutorService implements ExecutorService {

    private TaskTimeoutWatcher taskTimeoutWatcher;
    private ExecutorService delegate;
    private int timeout;

    public TimeoutExecutorService(ExecutorService delegate, int timeout) {
        this.delegate = delegate;
        this.timeout = timeout;
        taskTimeoutWatcher = new TaskTimeoutWatcher(timeout);
    }

    @Override
    public void shutdown() {
        delegate.shutdown();
        taskTimeoutWatcher.stop();
    }

    @Override
    public List<Runnable> shutdownNow() {
        taskTimeoutWatcher.stop();
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
        taskTimeoutWatcher.watchForTimeout(future);
        return future;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        Future<T> future = delegate.submit(task, result);
        taskTimeoutWatcher.watchForTimeout(future);
        return future;
    }

    @Override
    public Future<?> submit(Runnable task) {
        Future<?> future = delegate.submit(task);
        taskTimeoutWatcher.watchForTimeout(future);
        return future;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return invokeAll(tasks, timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        try {
            return delegate.invokeAny(tasks, timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new ExecutionException(e);
        }
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        submit(command);
    }
}
