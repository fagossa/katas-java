package com.trip.original.trip;

import com.trip.original.user.CollaboratorCallException;
import com.trip.original.user.User;

import java.util.List;

public class TripDAO {
    public static List<Trip> findTripsByUser(User user) {
        throw new CollaboratorCallException(
                "TripDAO should not be invoked on an unit test.");
    }
}
