package com.github.pscheidl.fortee.extension.beans;

import com.github.pscheidl.fortee.failsafe.Semisafe;

import javax.enterprise.context.Dependent;

@Dependent
public class IncorrectSemisafeMethodContractBean {

    @Semisafe({})
    public String incorrectReturnTypeMethod() {
        return null;
    }

}
