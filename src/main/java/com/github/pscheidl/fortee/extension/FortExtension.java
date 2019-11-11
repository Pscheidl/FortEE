package com.github.pscheidl.fortee.extension;

import com.github.pscheidl.fortee.failsafe.Failsafe;
import com.github.pscheidl.fortee.failsafe.FailsafeInterceptor;
import com.github.pscheidl.fortee.failsafe.Semisafe;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Listens during CDI startup, registering necessary interceptors.
 *
 * @author Pavel Pscheidl
 */
public class FortExtension implements Extension {

    private final Logger logger = Logger.getLogger(FortExtension.class.getName());

    /**
     * Inspects annotated types for usage of @Failsafe interceptor and checks the return types of intercepted methods.
     *
     * @param pat Annotated type to be processed
     * @param <X> Generic type of AnnotatedType
     */
    public <X> void inspectFailsafeAnnotated(@Observes ProcessAnnotatedType<X> pat) {
        AnnotatedType<X> annotatedType = pat.getAnnotatedType();

        if (FailsafeInterceptor.class.isAssignableFrom(annotatedType.getJavaClass())) {
            return;
        }

        if (annotatedType.isAnnotationPresent(Failsafe.class) || annotatedType.isAnnotationPresent(Semisafe.class)) {
            scanAllMethodsForIncorrectReturnType(annotatedType);
            scanAllMethodsForDeclaredThrowables(annotatedType);
        } else {
            findGuardedMethodsWithBadReturnType(annotatedType);
            findGuardedMethodsDeclaringExceptions(annotatedType);
        }
    }

    /**
     * Searches all methods declared in the underlying class for not having Optional<T> return type.
     *
     * @param annotatedType Class annotated with @Failsafe annotation
     * @param <X>           Generic type of AnnotatedType
     * @return Potentially empty list of public methods not returning Optional<T>.
     */
    private <X> void scanAllMethodsForIncorrectReturnType(AnnotatedType<X> annotatedType) {
        final long count = annotatedType.getMethods()
                .stream()
                .filter(annotatedMethod -> !annotatedMethod.getJavaMember().getReturnType().equals(Optional.class))
                .map(badMethod -> {
                    final String error = String.format("A guarded method %s does not return Optional<T>.",
                            badMethod.getJavaMember().toString());
                    logger.log(Level.INFO, error);
                    return badMethod;
                })
                .count();

        if (count > 0) {
            throw new IncorrectMethodSignatureException("Found methods that violate Optional<T> return contract.");
        }
    }

    /**
     * Searches all methods declared in the underlying class for throwables declared
     * @param annotatedType Class annotated with @Failsafe annotation
     * @param <X>           Generic type of AnnotatedType
     */
    private <X> void scanAllMethodsForDeclaredThrowables(AnnotatedType<X> annotatedType) {
        final long count = annotatedType.getMethods()
                .stream()
                .filter(annotatedMethod -> annotatedType.isAnnotationPresent(Failsafe.class) &&
                        annotatedMethod.getJavaMember().getExceptionTypes().length != 0)
                .map(badMethod -> {
                    final String error = String.format("A guarded method %s has declared exceptions thrown." +
                                    " Please remove the exception declaration from method's signature.",
                            badMethod.getJavaMember().toString());
                    logger.log(Level.INFO, error);
                    return badMethod;
                })
                .count();

        if (count > 0) {
            throw new IncorrectMethodSignatureException("Found guarded methods with declared exceptions thrown." +
                    " Please remove the exception declaration from their signatures signature.");
        }
    }

    /**
     * Searches methods in the underlying class annotated with @Failsafe annotation for not returning Optional<T>.
     *
     * @param annotatedType Class annotated with @Failsafe annotation
     * @param <X>           Generic type of AnnotatedType
     * @return Potentially empty list of public methods not returning Optional<T>.
     */
    private <X> void findGuardedMethodsWithBadReturnType(AnnotatedType<X> annotatedType) {
        final long count = annotatedType.getMethods()
                .stream()
                .filter(method -> (method.isAnnotationPresent(Failsafe.class) || method.isAnnotationPresent(Semisafe.class))
                        && !method.getJavaMember().getReturnType().equals(Optional.class))
                .map(badMethod -> {
                    final String error = String.format("A guarded method %s does not return Optional<T>.",
                            badMethod.getJavaMember().toString());
                    logger.log(Level.INFO, error);
                    return badMethod;
                })
                .count();

        if (count > 0) {
            throw new IncorrectMethodSignatureException("Found methods that violate Optional<T> return contract.");
        }
    }

    /**
     * Searches methods in the underlying class annotated with @Failsafe annotation for throwables declared
     * @param annotatedType Class annotated with @Failsafe annotation
     * @param <X>           Generic type of AnnotatedType
     */
    private <X> void findGuardedMethodsDeclaringExceptions(AnnotatedType<X> annotatedType) {
        final long count = annotatedType.getMethods()
                .stream()
                .filter(method -> (method.isAnnotationPresent(Failsafe.class))
                        && method.getJavaMember().getExceptionTypes().length != 0)
                .map(badMethod -> {
                    final String error = String.format("A guarded method %s has declared exceptions thrown." +
                                    " Please remove the exception declaration from method's signature.",
                            badMethod.getJavaMember().toString());
                    logger.log(Level.INFO, error);
                    return badMethod;
                })
                .count();

        if (count > 0) {
            throw new IncorrectMethodSignatureException("Found guarded methods with declared exceptions thrown." +
                    " Please remove the exception declaration from their signatures signature.");
        }
    }
}
