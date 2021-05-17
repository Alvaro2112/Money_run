package sdp.moneyrun.map;

import android.location.Location;

import com.google.firebase.database.FirebaseDatabase;

import org.junit.BeforeClass;
import org.junit.Test;

import sdp.moneyrun.ui.MainActivity;
import sdp.moneyrun.ui.map.MapActivity;

import static org.junit.Assert.assertEquals;

public class CoinGenerationHelperTest {

    @BeforeClass
    public static void setPersistence() {
        if (!MainActivity.calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            MainActivity.calledAlready = true;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void randomLocThrowsCorrectErrorForNegativeRadius() {
        long seed = 654;
        Location loc = new Location("");
        loc.setLongitude(4);
        loc.setLatitude(8);
        CoinGenerationHelper.getRandomLocation(loc, -10);

    }

    @Test
    public void randomLocCorrectlyComputeLocation() {
        Location loc = new Location("");
        loc.setLongitude(4);
        loc.setLatitude(8);
        int radius = 1000;
        for (int i = 0; i < 100000; i++) {
            Location random = CoinGenerationHelper.getRandomLocation(loc, radius);
            double distance = MapActivity.distance(loc.getLatitude(), loc.getLongitude(), random.getLatitude(), random.getLongitude());
            assert (distance < radius && distance > MapActivity.THRESHOLD_DISTANCE);
        }


    }

    @Test(expected = IllegalArgumentException.class)
    public void randomLocThrowsCorrectErrorForNullLocation() {
        CoinGenerationHelper.getRandomLocation(null, 10);

    }

    @Test
    public void coinValueWorks() {
        Location centerLoc = new Location("");
        centerLoc.setLatitude(0);
        centerLoc.setLongitude(0);
        Location coinLoc = new Location("");
        centerLoc.setLatitude(1);
        centerLoc.setLongitude(1);
        double distance = MapActivity.distance(coinLoc.getLatitude(), coinLoc.getLongitude(), centerLoc.getLatitude(), centerLoc.getLongitude());
        int value = CoinGenerationHelper.coinValue(coinLoc, centerLoc);
        assertEquals(Math.ceil((distance) / CoinGenerationHelper.VALUE_RADIUS), value, 0);
    }

    @Test(expected = NullPointerException.class)
    public void coinValuesFailsOnNullCoinLoc() {
        Location centerLoc = new Location("");
        centerLoc.setLatitude(0);
        centerLoc.setLongitude(0);
        int value = CoinGenerationHelper.coinValue(null, centerLoc);

    }

    @Test(expected = NullPointerException.class)
    public void coinValuesFailsOnNullCenterLoc() {
        Location coinLoc = new Location("");
        coinLoc.setLatitude(0);
        coinLoc.setLongitude(0);
        int value = CoinGenerationHelper.coinValue(coinLoc, null);

    }

}
