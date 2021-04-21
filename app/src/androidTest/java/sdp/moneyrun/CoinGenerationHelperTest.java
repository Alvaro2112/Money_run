package sdp.moneyrun;

import android.location.Location;

import org.junit.Test;

import sdp.moneyrun.map.CoinGenerationHelper;
import sdp.moneyrun.map.MapActivity;

public class CoinGenerationHelperTest {

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
       for(int i = 0; i < 100000; i++){
           Location random = CoinGenerationHelper.getRandomLocation(loc, radius);
           double distance = MapActivity.distance(loc.getLatitude(), loc.getLongitude(), random.getLatitude(), random.getLongitude());
           assert(distance < radius && distance > MapActivity.THRESHOLD_DISTANCE);
       }


    }

    @Test(expected = IllegalArgumentException.class)
    public void randomLocThrowsCorrectErrorForNullLocation() {
        CoinGenerationHelper.getRandomLocation(null, 10);

    }


}
