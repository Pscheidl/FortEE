package cz.pscheidl.fortee.timeout;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Pavel Pscheidl
 */
public class ThreadTimer {

    private final int idleTimeout;
    private final int timeout;
    private Thread checkThread;
    private volatile Deque<Job> jobs;
    private ReadWriteLock jobsLock;
    private volatile boolean checkThreadRunning;


    public ThreadTimer(int timeout) {
        jobs = new ArrayDeque<>(200);
        jobsLock = new ReentrantReadWriteLock();
        this.timeout = timeout;
        this.idleTimeout = Math.max(timeout - 1, 0);

        checkThread = new Thread(this::start);
        checkThread.start();
    }

    public void watchForTimeout(Future watchedThread) {
        long threadFinishTime = System.currentTimeMillis() + timeout;
        Job job = new Job(threadFinishTime, watchedThread);
        jobsLock.writeLock().lock();
        jobs.addLast(job);
        jobsLock.writeLock().unlock();
    }


    private void start() {
        checkThreadRunning = true;
        System.out.println("IN queue: " + jobs.size());
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

    @Override
    protected void finalize() throws Throwable {
        checkThreadRunning = false;
        if (!checkThread.isInterrupted()) {
            checkThread.interrupt();
        }
    }
}
