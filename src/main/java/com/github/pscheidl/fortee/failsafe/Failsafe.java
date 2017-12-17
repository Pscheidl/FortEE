package com.github.pscheidl.fortee.failsafe;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Binds {@link NoExceptionsFailsafeInterceptor} to a specific method or to each and every public-declared method when
 * placed on top of a CDI bean.
 *
 * @author Pavel Pscheidl
 */
@Inherited
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, TYPE})
public @interface Failsafe {
}
