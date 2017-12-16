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
     * Constructs a new instance of {@link ExecutionErrorEvent}
     *
     * @param executedMethod The erroneous method executed
     * @param throwable      Cause of erroneous behavior
     * @param failTime       Time the non-standard behavior was captured
     */
    protected ExecutionErrorEvent(Method executedMethod, Throwable throwable, LocalDateTime failTime) {
        this.calledMethod = executedMethod;
        this.throwable = throwable;
        this.failTime = failTime;
    }

    /**
     * @return Method the erroneous behavior appeared in.
     */
    public Method getCalledMethod() {
        return calledMethod;
    }


    /**
     * @return The {@link Throwable} cause of erroneous behavior.
     */
    public Throwable getThrowable() {
        return throwable;
    }


    /**
     * @return Time the erroneous method execution was intercepted at.
     */
    public LocalDateTime getFailTime() {
        return failTime;
    }

}
