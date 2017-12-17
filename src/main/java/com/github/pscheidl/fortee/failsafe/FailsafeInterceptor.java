package com.github.pscheidl.fortee.failsafe;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;
import java.time.LocalDateTime;

/**
 * Abstract Failsafe interceptor converting unexpected erroneous states into an empty {@link java.util.Optional}
 */
public abstract class FailsafeInterceptor {

    @Inject
    private Event<ExecutionErrorEvent> executionErrorEvent;

    /**
     * Assembles and fires ExecutionError event.
     *
     * @param invocationContext Interceptor's invocation context
     * @param throwable         Throwable catched by the interceptor
     */
    protected void throwExecutionErrorEvent(InvocationContext invocationContext, Throwable throwable) {
        ExecutionErrorEvent executionErrorEvent = new ExecutionErrorEvent(invocationContext.getMethod(),
                throwable, LocalDateTime.now());
        this.executionErrorEvent.fire(executionErrorEvent);
    }
}
