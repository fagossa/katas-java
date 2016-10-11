package com.bank;

import java.io.PrintStream;
import java.util.Date;

import static com.bank.Amount.amountOf;

public class Account {

    private Statement statement;

    private Amount balance = amountOf(0);

    public Account(Statement statement) {
        this.statement = statement;
    }

    public void deposit(Amount value, Date date) {
        // TODO: record transaction
    }

    public void withdrawal(Amount value, Date date) {
        // TODO: record transaction with negative value
    }

    public void printStatement(PrintStream printer) {
        statement.printTo(printer);
    }

    private void recordTransaction(Amount value, Date date) {
        Transaction transaction = new Transaction(value, date);
        Amount balanceAfterTransaction = transaction.balanceAfterTransaction(balance);
        balance = balanceAfterTransaction;
        statement.addLineContaining(transaction, balanceAfterTransaction);
    }

}
