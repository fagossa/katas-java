package com.trip.refactor.user;

public class DependendClassCallDuringUnitTestException extends RuntimeException {
    public DependendClassCallDuringUnitTestException(String message) {
        super(message);
    }
}
