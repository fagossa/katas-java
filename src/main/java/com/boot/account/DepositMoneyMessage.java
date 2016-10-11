package com.boot.account;

import java.io.Serializable;
import java.math.BigDecimal;

public class DepositMoneyMessage implements Serializable {

    private AccountTO accountTO;
    private BigDecimal ammount;

    public AccountTO getAccountTO() {
        return accountTO;
    }

    public void setAccountTO(AccountTO accountTO) {
        this.accountTO = accountTO;
    }

    public BigDecimal getAmmount() {
        return ammount;
    }

    public void setAmmount(BigDecimal ammount) {
        this.ammount = ammount;
    }

    @Override
    public String toString() {
        return "SendMoneyTO [accountTO=" + accountTO + ", ammount=" + ammount + "]";
    }

}
