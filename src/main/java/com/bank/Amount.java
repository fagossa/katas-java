package com.bank;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Amount {

    private DecimalFormat decimalFormat = buildDecimalFormat("#.00");

    private DecimalFormat buildDecimalFormat(String pattern) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern(pattern);
        return df;
    }

    private int value;

    public Amount(int value) {
        this.value = value;
    }

    public static Amount amountOf(int value) {
        return new Amount(value);
    }

    public Amount plus(Amount otherAmount) {
        // TODO: value + value of other amount
        return null;
    }

    public boolean isGreaterThan(Amount otherAmount) {
        // TODO: compare this value and other amount
        return false;
    }

    public Amount absoluteValue() {
        return amountOf(Math.abs(value));
    }

    public String moneyRepresentation() {
        return decimalFormat.format(value);
    }

    public Amount negative() {
        return amountOf(-value);
    }

    @Override
    public boolean equals(Object obj) {
        Amount other = (Amount) obj;
        if (value != other.value)
            return false;
        return true;
    }

}
