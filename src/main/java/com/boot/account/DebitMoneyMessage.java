package com.boot.account;

import java.io.Serializable;
import java.math.BigDecimal;

public class DebitMoneyMessage implements Serializable {
    private AccountTO accountTO;
    private BigDecimal amount;
    private String reason;

    private DebitMoneyMessage() {
    }

    public AccountTO getAccountTO() {
        return accountTO;
    }


    public BigDecimal getAmount() {
        return amount;
    }


    public String getReason() {
        return reason;
    }


    public boolean transferAmountLowerThanAccountBalance() {
        return getAmount().compareTo(accountTO.getAccountBalance()) > 0;
    }

    public static class GetMoneyBuilder {
        private AccountTO accountTO;
        private BigDecimal ammount;
        private String reason;

        public GetMoneyBuilder withAccountTO(AccountTO accountTO) {
            this.accountTO = accountTO;
            return this;
        }

        public GetMoneyBuilder withReason(String reason) {
            this.reason = reason;
            return this;
        }

        public GetMoneyBuilder withAmount(BigDecimal amount) {
            this.ammount = amount;
            return this;
        }

        public DebitMoneyMessage build() {
            final DebitMoneyMessage debitMoneyMessage = new DebitMoneyMessage();
            debitMoneyMessage.accountTO = accountTO;
            debitMoneyMessage.amount = ammount;
            debitMoneyMessage.reason = reason;
            return debitMoneyMessage;
        }
    }
}
