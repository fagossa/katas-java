package com.trip.original.user;

public class UserSession {
    private static final UserSession userSession = new UserSession();

    private UserSession() {
    }

    public static UserSession getInstance() {
        return userSession;
    }

    public User getLoggedUser() {
        throw new CollaboratorCallException(
                "UserSession.getLoggedUser() should not be called in an unit test");
    }

}
