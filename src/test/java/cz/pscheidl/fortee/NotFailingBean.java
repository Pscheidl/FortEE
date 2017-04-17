package cz.pscheidl.fortee;

import javax.inject.Named;
import java.util.Optional;

/**
 * @author Pavel Pscheidl
 */
@Named
public class NotFailingBean {

    public Optional<String> returnOptionalWithStringInside() {
        return Optional.of("Value returned normally");
    }

}
