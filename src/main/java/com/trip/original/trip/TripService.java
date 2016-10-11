package com.trip.original.trip;

import com.trip.original.user.User;
import com.trip.original.user.UserNotLoggedInException;
import com.trip.original.user.UserSession;

import java.util.ArrayList;
import java.util.List;

public class TripService {

    /**
     * Returns the trips of the user specified
     * @param user the user specified
     * @return The trips
     * @throws UserNotLoggedInException When not logged in
     */
    public List<Trip> getTripsByUser(User user) throws UserNotLoggedInException {
        // TODO: extract this default value
        List<Trip> tripList = new ArrayList<Trip>();
        // TODO: extract this parameter
        User loggedUser = UserSession.getInstance().getLoggedUser();
        boolean isFriend = false;
        // TODO extract this validation
        if (loggedUser != null) {
            for (User friend : user.getFriends()) {
                if (friend.equals(loggedUser)) {
                    isFriend = true;
                    break;
                }
            }
            if (isFriend) {
                // TODO: remove dependency to DAO
                tripList = TripDAO.findTripsByUser(user);
            }
            return tripList;
        } else {
            throw new UserNotLoggedInException();
        }
    }

}
