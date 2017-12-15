package com.github.pscheidl.fortee.failsafe.beans;

import com.github.pscheidl.fortee.failsafe.Failsafe;

import javax.enterprise.context.Dependent;
import java.util.Optional;

/**
 * @author Pavel Pscheidl
 */
@Dependent
public class FailingBean {

    @Failsafe
    public Optional<String> throwError() {
        throw new IllegalArgumentException("Thrown on purpose");
    }

    @Failsafe
    public Optional<String> returnNull() {
        return null;
    }

}
