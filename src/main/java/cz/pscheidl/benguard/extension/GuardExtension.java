package cz.pscheidl.benguard.extension;

import cz.pscheidl.benguard.Failsafe;
import cz.pscheidl.benguard.FailsafeInterceptor;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Pavel Pscheidl
 */
public class GuardExtension implements Extension {
    Logger logger = Logger.getLogger(GuardExtension.class.getName());

    /**
     * Inspects annotated types for usage of @Failsafe interceptor and checks the return types of intercepted methods.
     *
     * @param pat Annotated type to be processed
     * @param <X> Generic type of AnnotatedType
     */
    public <X> void inspectFailsafeAnnotated(@Observes ProcessAnnotatedType<X> pat) {
        AnnotatedType<X> annotatedType = pat.getAnnotatedType();

        if (annotatedType.getJavaClass().equals(FailsafeInterceptor.class)) {
            return;
        }

        if (annotatedType.isAnnotationPresent(Failsafe.class)) {
            List<AnnotatedMethod<? super X>> badMethods = findMethodsWithoutOptionalReturnType(annotatedType);
            if (!badMethods.isEmpty()) {
                logBadMethods(badMethods);
                throw new RuntimeException("Found methods that violate Optional<T> return contract.");
            }
        } else {
            List<AnnotatedMethod<? super X>> badMethods = findGuardedMethodsWithBadReturnType(annotatedType);
            if (!badMethods.isEmpty()) {
                logBadMethods(badMethods);
                throw new RuntimeException("Found methods that violate Optional<T> return contract.");
            }
        }
    }

    /**
     * Searches all methods declared in the underlying class for not having Optional<T> return type.
     *
     * @param annotatedType Class annotated with @Failsafe annotation
     * @param <X>           Generic type of AnnotatedType
     * @return Potentially empty list of public methods not returning Optional<T>.
     */
    private <X> List<AnnotatedMethod<? super X>> findMethodsWithoutOptionalReturnType(AnnotatedType<X> annotatedType) {
        return annotatedType.getMethods()
                .stream()
                .filter(annotatedMethod -> !annotatedMethod.getJavaMember().getReturnType().equals(Optional.class))
                .collect(Collectors.toList());
    }

    /**
     * Searches methods in the underlying class annotated with @Failsafe annotation for not returning Optional<T>.
     *
     * @param annotatedType Class annotated with @Failsafe annotation
     * @param <X>           Generic type of AnnotatedType
     * @return Potentially empty list of public methods not returning Optional<T>.
     */
    private <X> List<AnnotatedMethod<? super X>> findGuardedMethodsWithBadReturnType(AnnotatedType<X> annotatedType) {
        return annotatedType.getMethods()
                .stream()
                .filter(method -> method.isAnnotationPresent(Failsafe.class))
                .filter(annotatedMethod -> !annotatedMethod.getJavaMember().getReturnType().equals(Optional.class))
                .collect(Collectors.toList());
    }

    /**
     * Logs names of methods and their declaring classes without proper return type
     *
     * @param badMethods List of bad methods to print
     * @param <X>        Generic type of AnnotatedType
     */
    private <X> void logBadMethods(List<AnnotatedMethod<? super X>> badMethods) {
        badMethods.forEach(method -> {
            StringBuilder badMethodMessageBuilder = new StringBuilder("A guarded method ");
            badMethodMessageBuilder.append(method.getJavaMember().getName());
            badMethodMessageBuilder.append(" in class ");
            badMethodMessageBuilder.append(method.getJavaMember().getDeclaringClass().getCanonicalName());
            badMethodMessageBuilder.append(" does not return Optional<T>.");
            logger.log(Level.SEVERE, badMethodMessageBuilder.toString());
        });
    }
}
