package com.github.pscheidl.fortee.failsafe.beans;

import javax.enterprise.context.Dependent;
import java.util.Optional;

/**
 * @author Pavel Pscheidl
 */
@Dependent
public class NotFailingBean {

    public Optional<String> returnOptionalWithStringInside() {
        return Optional.of("Value returned normally");
    }

}
