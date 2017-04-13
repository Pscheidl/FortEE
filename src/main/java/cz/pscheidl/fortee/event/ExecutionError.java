package cz.pscheidl.fortee.event;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * @author Pavel Pscheidl
 */
public class ExecutionError {
    private Method calledMethod;
    private Throwable throwable;
    private LocalDateTime failTime;

    public Method getCalledMethod() {
        return calledMethod;
    }

    public void setCalledMethod(Method calledMethod) {
        this.calledMethod = calledMethod;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public LocalDateTime getFailTime() {
        return failTime;
    }

    public void setFailTime(LocalDateTime failTime) {
        this.failTime = failTime;
    }
}
