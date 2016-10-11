package com.trip.refactor.user;

public class UserNotLoggedInException extends RuntimeException {
    public UserNotLoggedInException(String message) {
        super(message);
    }

    public UserNotLoggedInException() {

    }
}
