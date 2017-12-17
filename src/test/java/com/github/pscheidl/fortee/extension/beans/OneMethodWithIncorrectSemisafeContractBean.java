package com.github.pscheidl.fortee.extension.beans;

import com.github.pscheidl.fortee.failsafe.Failsafe;
import com.github.pscheidl.fortee.failsafe.Semisafe;

import javax.enterprise.context.Dependent;
import java.util.Optional;

@Dependent
@Semisafe({})
public class OneMethodWithIncorrectSemisafeContractBean {

    public Optional<String> correctMethodSignature() {
        return Optional.empty();
    }

    public String incorrectMethodSignature() {
        return null;
    }

}
