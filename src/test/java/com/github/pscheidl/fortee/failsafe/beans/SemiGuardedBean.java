package com.github.pscheidl.fortee.failsafe.beans;

import com.github.pscheidl.fortee.failsafe.Semisafe;
import junit.framework.AssertionFailedError;

import javax.enterprise.context.Dependent;
import java.util.Optional;

@Dependent
public class SemiGuardedBean {

    @Semisafe({AssertionError.class})
    public Optional<String> letThrough() {
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

    @Semisafe({})
    public Optional<String> returnNull() {
        return null;
    }

    @Semisafe({})
    public Optional<String> returnSomething() {
        return Optional.of("Something");
    }
}
