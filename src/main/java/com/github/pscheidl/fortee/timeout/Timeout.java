package com.github.pscheidl.fortee.timeout;

import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * Qualifier for an timeout-enabled ExecutorService injection.
 *
 * @author Pavel Pscheidl
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, FIELD, METHOD, PARAMETER})
public @interface Timeout {

    /**
     * @return Time interval before the underlying tasks are timed out. In
     * milliseconds
     */
    @Nonbinding
    public int millis() default 1000;

    /**
     * @return Number of threads in pool
     */
    @Nonbinding
    public int threads() default 100;
}
