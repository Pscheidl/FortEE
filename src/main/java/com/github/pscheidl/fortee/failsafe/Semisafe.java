package com.github.pscheidl.fortee.failsafe;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Binds {@link SemisafeInterceptor} to a specific method or to each and every public-declared method when placed on top
 * of a CDI bean.
 *
 * @author Pavel Pscheidl
 */
@Inherited
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, TYPE})
public @interface Semisafe {

    /**
     * Classes extending {@link Throwable} being ignored in the failsafe process. Such throwables are re-thrown and do
     * not trigger the failsafe process.
     */
    @Nonbinding
    Class<? extends Throwable>[] value();
}
