package com.github.pscheidl.fortee.failsafe;

import javax.annotation.Priority;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Transforms uncatched exceptions into an empty optional. Method with this
 * interceptor present must have the return type of Optional
 *
 * @author Pavel Pscheidl
 */
@Interceptor
@Failsafe
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class FailsafeInterceptor implements Serializable {

    @Inject
    private Event<ExecutionErrorEvent> executionErrorEvent;

    /**
     * If there is an exception thrown in the underlying method call, the
     * exception is converted into an empty Optional.
     *
     * @param invocationContext Interceptor's invocation context
     * @return Value returned by the underlying method call. Empty optional in
     * case of an exception.
     */
    @AroundInvoke
    public Object guard(InvocationContext invocationContext) {
        try {
            Object returnedObject = invocationContext.proceed();

            if (returnedObject == null || !(returnedObject instanceof Optional)) {
                return Optional.empty();
            }
            return returnedObject;
        } catch (Throwable throwable) {
            throwExecutionErrorEvent(invocationContext, throwable);
            return Optional.empty();
        }
    }

    /**
     * Assembles and fires ExecutionError event.
     *
     * @param invocationContext Interceptor's invocation context
     * @param throwable         Throwable catched by the interceptor
     */
    private void throwExecutionErrorEvent(InvocationContext invocationContext, Throwable throwable) {
        ExecutionErrorEvent executionErrorEvent = new ExecutionErrorEvent(invocationContext.getMethod(),
                throwable, LocalDateTime.now());
        this.executionErrorEvent.fire(executionErrorEvent);
    }
}
