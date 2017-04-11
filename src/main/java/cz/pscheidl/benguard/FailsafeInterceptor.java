package cz.pscheidl.benguard;

import cz.pscheidl.benguard.event.ExecutionError;
import org.slf4j.Logger;

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
 * Transforms uncatched exceptions into an empty optional.
 * Method with this interceptor present must have the return type of Optional
 *
 * @author Pavel Pscheidl
 */
@Interceptor
@Failsafe
@Priority(1000)
public class FailsafeInterceptor implements Serializable {

    @Inject
    private Logger logger;

    @Inject
    private Event<ExecutionError> executionErrorEvent;

    /**
     * If there is an exception thrown in the underlying method call, the exception is converted into an empty Optional.
     *
     * @param invocationContext Interceptor's invocation context
     * @return Value returned by the underlying method call. Empty optional in case of an exception.
     */
    @AroundInvoke
    public Object guard(InvocationContext invocationContext) {
        try {
            Object returnedObject = invocationContext.proceed();
            return returnedObject;
        } catch (Throwable throwable) {
            throwExecutionErrorEvent(invocationContext, throwable);
            logger.warn("Failsafe interceptor exception caught.", throwable);
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
        ExecutionError executionError = new ExecutionError();
        executionError.setFailTime(LocalDateTime.now());
        executionError.setCalledMethod(invocationContext.getMethod());
        executionError.setThrowable(throwable);
        executionErrorEvent.fire(executionError);
    }
}