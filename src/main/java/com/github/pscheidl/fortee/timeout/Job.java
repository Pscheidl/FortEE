package com.github.pscheidl.fortee.timeout;

import java.util.concurrent.Future;

/**
 * @author Pavel Pscheidl
 */
public class Job {

    private long end;
    private Future future;

    public Job(long end, Future future) {
        this.end = end;
        this.future = future;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public Future getFuture() {
        return future;
    }

    public void setFuture(Future future) {
        this.future = future;
    }
}
