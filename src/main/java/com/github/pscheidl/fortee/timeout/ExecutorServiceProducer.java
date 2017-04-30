package com.github.pscheidl.fortee.timeout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;

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
