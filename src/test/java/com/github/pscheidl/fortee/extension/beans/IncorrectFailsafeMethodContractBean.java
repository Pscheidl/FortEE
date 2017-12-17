package com.github.pscheidl.fortee.extension.beans;

import com.github.pscheidl.fortee.failsafe.Failsafe;

import javax.enterprise.context.Dependent;

@Dependent
public class IncorrectFailsafeMethodContractBean {

    @Failsafe
    public String incorrectReturnTypeMethod() {
        return null;
    }

}
