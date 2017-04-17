package cz.pscheidl.fortee;

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
