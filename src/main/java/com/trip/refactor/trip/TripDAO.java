package com.trip.refactor.trip;

import com.trip.refactor.user.DependendClassCallDuringUnitTestException;
import com.trip.refactor.user.User;

import java.util.List;

public class TripDAO {
    public static List<Trip> findTripsByUser(User user) {
        throw new DependendClassCallDuringUnitTestException(
                "TripDAO should not be invoked on an unit test.");
    }

    public List<Trip> tripsBy(User user) {
        return TripDAO.findTripsByUser(user);
    }

}
