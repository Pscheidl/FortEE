package com.github.pscheidl.fortee.failsafe;

import com.github.pscheidl.fortee.Failsafe;
import java.util.Optional;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * @author Pavel Pscheidl
 */
@Named
@RequestScoped
public class FailingBean {

    @Failsafe
    public Optional<String> throwError() {
        throw new RuntimeException("Thrown on purpose");
    }

}
