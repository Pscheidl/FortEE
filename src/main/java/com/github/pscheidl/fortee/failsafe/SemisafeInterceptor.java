package com.github.pscheidl.fortee.failsafe;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Optional;

@Interceptor
@Semisafe({})
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
class SemisafeInterceptor extends FailsafeInterceptor implements Serializable {

    /**
     * If there is an exception thrown in the underlying method call, the exception is converted into an empty
     * Optional.
     *
     * @param invocationContext Interceptor's invocation context
     * @return Value returned by the underlying method call. Empty optional in case of an exception.
     */
    @AroundInvoke
    public Object filter(InvocationContext invocationContext) throws Throwable {
        try {
            final Object returnedObject = invocationContext.proceed();

            if (returnedObject == null || !(returnedObject instanceof Optional)) {
                return Optional.empty();
            }
            return returnedObject;
        } catch (Throwable throwable) {
            if (isIgnoredThrowable(throwable, invocationContext.getMethod())) {
                throw throwable;
            }
            super.throwExecutionErrorEvent(invocationContext, throwable);
            return Optional.empty();
        }
    }

    private boolean isIgnoredThrowable(Throwable throwable, Method method) {
        Semisafe methodAnnotation = method.getAnnotation(Semisafe.class);

        if (methodAnnotation == null) {
            methodAnnotation = method.getDeclaringClass().getAnnotation(Semisafe.class);
        }

        for (Class throwableClass : methodAnnotation.value()) {
            if (throwableClass.isAssignableFrom(throwable.getClass())) {
                return true;
            }
        }

        return false;
    }
}
