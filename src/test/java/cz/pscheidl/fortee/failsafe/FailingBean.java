package cz.pscheidl.fortee.failsafe;

import cz.pscheidl.fortee.Failsafe;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.inject.Named;
import java.util.Optional;

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
