package cz.pscheidl.benguard;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.util.Optional;

/**
 * @author Pavel Pscheidl
 *
 */
@Interceptor
@Guard
public class GuardInterceptor implements Serializable {


    /**
     * If there is an exception thrown in the underlying method call, the exception is converted into an empty optional.
     * @param invocationContext Interceptor's invocation context
     * @return Value returned by the underlying method call. Empty optional in case of an exception.
     */
    @AroundInvoke
    public Object guard(InvocationContext invocationContext) {
        try {
            Object returnedObject = invocationContext.proceed();
            return Optional.of(returnedObject);
        } catch (Throwable t) {
            return Optional.empty();
        }
    }
}
