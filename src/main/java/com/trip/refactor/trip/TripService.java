package com.trip.refactor.trip;

import com.trip.refactor.user.User;
import com.trip.refactor.user.UserNotLoggedInException;

import java.util.ArrayList;
import java.util.List;

public class TripService {
    private TripDAO tripDAO;

    public TripService(TripDAO tripDAO) {
        this.tripDAO = tripDAO;
    }

    public List<Trip> getTripsByUser(User user, User loggedInUser) throws UserNotLoggedInException {
        validate(loggedInUser);

        return user.isFriendsWith(loggedInUser)
                ? tripsFrom(user)
                : noTrips();
    }

    private ArrayList<Trip> noTrips() {
        return new ArrayList<Trip>();
    }

    private void validate(User loggedInUser) {
        if (loggedInUser == null) {
            throw new UserNotLoggedInException();
        }
    }

    private List<Trip> tripsFrom(User user) {
        return tripDAO.tripsBy(user);
    }
}
