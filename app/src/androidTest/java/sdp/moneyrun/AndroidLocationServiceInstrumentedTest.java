package sdp.moneyrun;

import android.content.Intent;
import android.location.Criteria;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import sdp.moneyrun.location.AndroidLocationService;
import sdp.moneyrun.ui.MainActivity;
import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.user.User;
import sdp.moneyrun.location.LocationRepresentation;


import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.database.FirebaseDatabase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class AndroidLocationServiceInstrumentedTest {

    @BeforeClass
    public static void setPersistence() {
        if (!MainActivity.calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            MainActivity.calledAlready = true;
        }
    }

    private Intent getStartIntent() {
        User currentUser = new User("999", "CURRENT_USER", "Epfl"
                , 0, 0, 0);
        Intent toStart = new Intent(ApplicationProvider.getApplicationContext(), MenuActivity.class);
        toStart.putExtra("user", currentUser);
        return toStart;
    }

    @Test
    public void LocationServiceFromCriteriaIsCorrect() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            scenario.onActivity(a -> {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                AndroidLocationService locationService = AndroidLocationService.buildFromContextAndCriteria(a.getApplicationContext(), criteria);

                assertEquals(locationService.getLocationCriteria(), criteria);
            });
        }
    }

    @Test
    public void LocationServiceFromProviderIsCorrect() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            scenario.onActivity(a -> {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                AndroidLocationService locationService = AndroidLocationService.buildFromContextAndProvider(a.getApplicationContext(), "");

                assertEquals(locationService.getLocationProvider(), "");
            });
        }
    }

    @Test
    public void getMockedCurrentLocationWorks() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            scenario.onActivity(a -> {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);

                AndroidLocationService locationService = AndroidLocationService.buildFromContextAndCriteria(a.getApplicationContext(), criteria);

                LocationRepresentation locationRepresentation = new LocationRepresentation(10., 20.);
                locationService.setMockedLocation(locationRepresentation);

                assertTrue(locationService.isLocationMocked());
                assertEquals(locationService.getCurrentLocation(), locationRepresentation);

                locationService.resetMockedLocation();
                assertFalse(locationService.isLocationMocked());
            });
        }
    }

    @Test(expected = RuntimeException.class)
    public void MockedLocationFailsOnNullLocation() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            scenario.onActivity(a -> {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);

                AndroidLocationService locationService = AndroidLocationService.buildFromContextAndCriteria(a.getApplicationContext(), criteria);
                locationService.setMockedLocation(null);
            });
        }
    }
}
