package cz.pscheidl.fortee.timeout;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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
        return tasks.stream()
                .map(this::submit)
                .collect(Collectors.toList());
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return tasks.stream()
                .map(this::submit)
                .collect(Collectors.toList());
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        List<Future<T>> futures = tasks.stream()
                .map(this::submit)
                .collect(Collectors.toList());

        Thread.sleep(timeout);

        Optional<Future<T>> anyCompletedFuture = futures.stream()
                .filter(future -> !future.isCancelled())
                .findAny();

        if (!anyCompletedFuture.isPresent()) {
            throw new ExecutionException("No tasks invoked successfully.", null);
        }

        return anyCompletedFuture.get().get();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        List<Future<T>> futures = tasks.stream()
                .map(this::submit)
                .collect(Collectors.toList());

        unit.sleep(timeout);

        Optional<Future<T>> anyCompletedFuture = futures.stream()
                .filter(future -> !future.isCancelled())
                .findAny();

        if (!anyCompletedFuture.isPresent()) {
            throw new ExecutionException("No tasks invoked successfully.", null);
        }

        return anyCompletedFuture.get().get();
    }

    @Override
    public void execute(Runnable command) {
        submit(command);
    }
}
