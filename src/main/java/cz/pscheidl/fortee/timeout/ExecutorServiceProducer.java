package cz.pscheidl.fortee.timeout;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.sql.Time;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Pavel Pscheidl
 */
@Named
@ApplicationScoped
public class ExecutorServiceProducer {

    @Produces
    @Timeout
    public ExecutorService produceExecutorService(InjectionPoint injectionPoint) {
        Timeout timeoutAnnotation = injectionPoint.getAnnotated().getAnnotation(Timeout.class);

        ExecutorService delegate = Executors.newFixedThreadPool(timeoutAnnotation.threads());
        return new TimeoutExecutorService(delegate, timeoutAnnotation.millis());
    }


}
