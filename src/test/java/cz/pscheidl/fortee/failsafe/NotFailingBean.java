package cz.pscheidl.fortee.failsafe;

import java.util.Optional;
import javax.inject.Named;

/**
 * @author Pavel Pscheidl
 */
@Named
public class NotFailingBean {

    public Optional<String> returnOptionalWithStringInside() {
        return Optional.of("Value returned normally");
    }

}
