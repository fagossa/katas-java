package com.trip.refactor.trip;

import com.trip.refactor.user.User;
import com.trip.refactor.user.UserNotLoggedInException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static com.trip.refactor.user.UserBuilder.aUser;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class TripServiceTest {

    private static final User UNUSED_USER = null;
    private static final User ANOTHER_USER = new User();
    private static final Trip TO_BRAZIL = new Trip();
    private static final User GUEST = null;
    private static final User LOGGED_IN_USER = new User();
    private static final Trip TO_LONDON = new Trip();

    @Mock
    private TripDAO tripDAO;

    @Test(expected = UserNotLoggedInException.class)
    public void should_throw_an_exception_when_user_is_not_logged_in() {
        TripService realTripService = new TripService(tripDAO);
        realTripService.getTripsByUser(UNUSED_USER, GUEST);
    }

    @Test
    public void should_not_return_any_trips_when_users_are_not_friends() {
        // Given
        TripService realTripService = new TripService(tripDAO);
        User friend = aUser()
                .friendsWith(ANOTHER_USER)
                .withTrips(TO_BRAZIL)
                .build();

        // When
        List<Trip> friendTrips = realTripService.getTripsByUser(friend, LOGGED_IN_USER);

        // Then
        assertThat(friendTrips.size(), is(0));
    }

    @Test
    public void should_return_trips_when_users_are_friends() {
        User friend = aUser()
                .friendsWith(ANOTHER_USER, LOGGED_IN_USER)
                .withTrips(TO_BRAZIL, TO_LONDON)
                .build();
        given(tripDAO.tripsBy(friend)).willReturn(friend.trips());
        TripService realTripService = new TripService(tripDAO);

        // When
        List<Trip> friendTrips = realTripService.getTripsByUser(friend, LOGGED_IN_USER);

        // Then
        assertThat(friendTrips.size(), is(2));
    }

}
