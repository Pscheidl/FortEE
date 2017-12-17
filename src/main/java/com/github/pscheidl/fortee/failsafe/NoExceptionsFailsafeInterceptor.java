package com.github.pscheidl.fortee.failsafe;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.util.Optional;

/**
 * Transforms uncatched exceptions into an empty optional. Method with this interceptor present must have the return
 * type of Optional
 *
 * @author Pavel Pscheidl
 */
@Interceptor
@Failsafe
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
class NoExceptionsFailsafeInterceptor extends FailsafeInterceptor implements Serializable {

    /**
     * If there is an exception thrown in the underlying method call, the exception is converted into an empty
     * Optional.
     *
     * @param invocationContext Interceptor's invocation context
     * @return Value returned by the underlying method call. Empty optional in case of an exception.
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
            super.throwExecutionErrorEvent(invocationContext, throwable);
            return Optional.empty();
        }
    }
}
