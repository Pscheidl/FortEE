package com.github.pscheidl.fortee.failsafe;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.util.Optional;

public class DeclaredExceptionFailsafeInterceptor extends FailsafeInterceptor {


    private static boolean isExceptionIgnorable(final Class<? extends Throwable> throwable,
                                                final Class<?>[] declaredThrowableClasses) {
        for (Class<?> declaredException : declaredThrowableClasses) {
            if (throwable.equals(declaredException)) {
                return true;
            }
        }

        return false;
    }

    @AroundInvoke
    public Object guard(InvocationContext invocationContext) throws Throwable {
        try {
            Object returnedObject = invocationContext.proceed();

            if (returnedObject == null || !(returnedObject instanceof Optional)) {
                return Optional.empty();
            }

            return returnedObject;
        } catch (Throwable throwable) {
            final Class<?>[] exceptionTypes = invocationContext.getMethod().getExceptionTypes();
            if (isExceptionIgnorable(throwable.getClass(), exceptionTypes)) {
                throw throwable;
            }
            super.throwExecutionErrorEvent(invocationContext, throwable);
            return Optional.empty();
        }
    }
}
