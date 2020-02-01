package com.github.pscheidl.fortee.failsafe.beans;

import com.github.pscheidl.fortee.failsafe.Semisafe;
import junit.framework.AssertionFailedError;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.enterprise.context.Dependent;
import java.io.IOException;
import java.util.Optional;

@Dependent
public class SemiGuardedBean {

    @Semisafe({AssertionError.class})
    public Optional<String> letThrough() {
        throw new AssertionError();
    }

    @Semisafe
    public Optional<String> letThroughDeclaration() throws AssertionError {
        throw new AssertionError();
    }

    @Semisafe({AssertionError.class})
    public Optional<String> letInheritedThrough() {
        throw new AssertionFailedError();
    }

    @Semisafe({AssertionError.class})
    public Optional<String> doNotLetThrough() {
        throw new ClassFormatError();
    }

    @Semisafe
    public Optional<String> returnNull() {
        return null;
    }

    @Semisafe
    public Optional<String> returnSomething() {
        return Optional.of("Something");
    }

    @Semisafe({IOException.class})
    public Optional<String> throwSilentException() {
        return ExceptionUtils.rethrow(new IOException());
    }

    @Semisafe
    public Optional<String> convertSilentException() {
        return ExceptionUtils.rethrow(new IOException());
    }
}
