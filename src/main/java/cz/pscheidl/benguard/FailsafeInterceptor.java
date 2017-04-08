package cz.pscheidl.benguard;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private Logger logger = Logger.getLogger(FailsafeInterceptor.class.getName());

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
            logger.log(Level.WARNING, "Failsafe interceptor exception caught.", throwable);
            return Optional.empty();
        }
    }
}