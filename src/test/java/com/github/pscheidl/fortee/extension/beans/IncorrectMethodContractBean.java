package com.github.pscheidl.fortee.extension.beans;

import com.github.pscheidl.fortee.failsafe.Failsafe;

import javax.enterprise.context.Dependent;

@Dependent
public class IncorrectMethodContractBean {

    @Failsafe
    public String incorrectReturnTypeMethod() {
        return null;
    }

}
