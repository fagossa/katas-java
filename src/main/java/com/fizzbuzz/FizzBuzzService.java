package com.fizzbuzz;

public class FizzBuzzService {

    public static final String FIZZ = "fizz";
    public static final String BUZZ = "buzz";

    public String guess(int aNumber) {
        if (isMultipleOf3(aNumber) && isMultipleOf5(aNumber)) {
            return FIZZ + BUZZ;
        }

        if(isMultipleOf5(aNumber)) {
            return BUZZ;
        }

        if (isMultipleOf3(aNumber)) {
            return FIZZ;
        }
        return String.valueOf(aNumber);
    }

    private boolean isMultipleOf5(int aNumber) {
        return aNumber % 5 == 0;
    }

    private boolean isMultipleOf3(int aNumber) {
        return aNumber % 3 == 0;
    }
}
