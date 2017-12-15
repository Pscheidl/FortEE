package com.github.pscheidl.fortee.failsafe;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * An event representing erroneous execution of a method annotated with {@link Failsafe}.
 *
 * @author Pavel Pscheidl
 */
public class ExecutionErrorEvent {

    private final Method calledMethod;
    private final Throwable throwable;
    private final LocalDateTime failTime;

    /**
     * @param executedMethod The erroneous method executed
     * @param throwable      Cause of erroneous behavior
     * @param failTime       Time the non-standard behavior was captured
     */
    protected ExecutionErrorEvent(Method executedMethod, Throwable throwable, LocalDateTime failTime) {
        this.calledMethod = executedMethod;
        this.throwable = throwable;
        this.failTime = failTime;
    }

    public Method getCalledMethod() {
        return calledMethod;
    }


    public Throwable getThrowable() {
        return throwable;
    }


    public LocalDateTime getFailTime() {
        return failTime;
    }

}
