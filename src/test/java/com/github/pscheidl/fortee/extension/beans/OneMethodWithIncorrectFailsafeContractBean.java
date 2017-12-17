package com.github.pscheidl.fortee.extension.beans;

import com.github.pscheidl.fortee.failsafe.Failsafe;

import javax.enterprise.context.Dependent;
import java.util.Optional;

@Dependent
@Failsafe
public class OneMethodWithIncorrectFailsafeContractBean {

    public Optional<String> correctMethodSignature() {
        return Optional.empty();
    }

    public String incorrectMethodSignature() {
        return null;
    }

}
