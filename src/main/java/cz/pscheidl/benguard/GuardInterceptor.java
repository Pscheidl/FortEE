package cz.pscheidl.benguard;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.util.Optional;

/**
 * @author Pavel Pscheidl <pavel.junior@pscheidl.cz>
 */
@Interceptor
@Guard
public class GuardInterceptor implements Serializable {

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
