package cz.pscheidl.fortee.timeout;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Watches Future tasks for timeout.
 *
 * @author Pavel Pscheidl
 */
public class TaskTimeoutWatcher {

    private final int idleTimeout;
    private final int timeout;
    private Thread checkThread;
    private volatile Deque<Job> jobs;
    private ReadWriteLock jobsLock;
    private volatile boolean checkThreadRunning;

    public TaskTimeoutWatcher(int timeout) {
        jobs = new ArrayDeque<>(200);
        jobsLock = new ReentrantReadWriteLock();
        this.timeout = timeout;
        this.idleTimeout = Math.max(timeout - 1, 1);

        checkThread = new Thread(this::watch);
        checkThread.start();
    }

    /**
     * Adds new Future to the queue of tasks waiting for timeout check. If the
     * execution time exceeds watcher's timeout, it is cancelled.
     *
     * @param watchedFuture Future to be watched for timeout
     */
    public void watchForTimeout(Future watchedFuture) {
        long threadFinishTime = System.currentTimeMillis() + timeout;
        Job job = new Job(threadFinishTime, watchedFuture);
        jobsLock.writeLock().lock();
        jobs.addLast(job);
        jobsLock.writeLock().unlock();
    }

    /**
     * Iterates the queue of tasks to be checked for timeout
     */
    private void watch() {
        checkThreadRunning = true;
        while (checkThreadRunning) {
            jobsLock.readLock().lock();
            if (jobs.isEmpty()) {
                try {
                    jobsLock.readLock().unlock();
                    Thread.sleep(idleTimeout);
                    continue;
                } catch (InterruptedException e) {
                    continue;
                }
            } else {
                jobsLock.readLock().unlock();
            }

            jobsLock.readLock().lock();
            Job firstJob = jobs.peekFirst();
            jobsLock.readLock().unlock();
            long sleepTime = firstJob.getEnd() - System.currentTimeMillis();
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                    firstJob.getFuture().cancel(true);
                    jobsLock.writeLock().lock();
                    jobs.removeFirst();
                    jobsLock.writeLock().unlock();
                } catch (InterruptedException e) {
                    continue;
                }
            } else {
                jobsLock.writeLock().lock();
                Job job = jobs.removeFirst();
                jobsLock.writeLock().unlock();
                job.getFuture().cancel(true);
            }
        }

    }

    public void stop() {
        checkThreadRunning = false;
        if (!checkThread.isInterrupted()) {
            checkThread.interrupt();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        stop();
    }
}
