package cz.pscheidl.fortee.timeout;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * @author Pavel Pscheidl
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, FIELD, METHOD})
public @interface Timeout {

    /**
     * Time interval before the underlying tasks are timed out. In milliseconds
     */
    @Nonbinding
    public int millis() default 1000;

    @Nonbinding
    public int threads() default 100;
}
