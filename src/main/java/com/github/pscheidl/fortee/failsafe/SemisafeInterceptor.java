package com.github.pscheidl.fortee.failsafe;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Transforms uncatched exceptions into an empty optional, except for allowed exceptions defined in {@link Semisafe}
 * annotation. Method with this interceptor present must have the return type of Optional.
 *
 * @author Pavel Pscheidl
 */
@Interceptor
@Semisafe({})
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
class SemisafeInterceptor extends FailsafeInterceptor implements Serializable {

    /**
     * If there is a {@link Throwable} thrown in the underlying method call, the exception is converted into an empty
     * Optional, unless the {@link Error} or {@link Exception} is listed as ignorable in {@link Semisafe} annotation.
     *
     * @param invocationContext Interceptor's invocation context
     * @return Value returned by the underlying method call. Empty optional in case of an exception.
     */
    @AroundInvoke
    public Object filter(final InvocationContext invocationContext) throws Throwable {
        try {
            final Object returnedObject = invocationContext.proceed();

            if (returnedObject == null || !(returnedObject instanceof Optional)) {
                return Optional.empty();
            }
            return returnedObject;
        } catch (Throwable throwable) {
            if (isIgnoredThrowable(throwable.getClass(), invocationContext.getMethod())) {
                throw throwable;
            }
            super.throwExecutionErrorEvent(invocationContext, throwable);
            return Optional.empty();
        }
    }

    /**
     * @param throwableClass Intercepted {@link Throwable}
     * @param method         Intercepted {@link Method}
     * @return True if {@link Throwable} instance is among throwables to be ignored
     */
    private boolean isIgnoredThrowable(final Class<? extends Throwable> throwableClass, final Method method) {
        Semisafe methodAnnotation = method.getAnnotation(Semisafe.class);

        if (methodAnnotation == null) {
            methodAnnotation = method.getDeclaringClass().getAnnotation(Semisafe.class);
        }

        for (final Class letThroughClass : methodAnnotation.value()) {
            if (letThroughClass.isAssignableFrom(throwableClass)) {
                return true;
            }
        }

        for (Class<?> declaredException : method.getExceptionTypes()) {
            if (throwableClass.isAssignableFrom(declaredException)) {
                return true;
            }
        }
        return false;
    }
}
