package sdp.moneyrun;

import android.location.Location;

import org.junit.Test;

import sdp.moneyrun.map.MapActivity;
import sdp.moneyrun.map.RandomLocation;

public class RandomLocationTest {

    @Test(expected = IllegalArgumentException.class)
    public void randomLocThrowsCorrectErrorForNegativeRadius() {
        long seed = 654;
        Location loc = new Location("");
        loc.setLongitude(4);
        loc.setLatitude(8);
        RandomLocation.getRandomLocation(loc, -10);

    }

    @Test
    public void randomLocCorrectlyComputeLocation() {
        Location loc = new Location("");
        loc.setLongitude(4);
        loc.setLatitude(8);
        int radius = 1000;
       for(int i = 0; i < 100000; i++){
           Location random = RandomLocation.getRandomLocation(loc, radius);
           assert(MapActivity.distance(loc.getLatitude(), loc.getLongitude(), random.getLatitude(), random.getLongitude()) < radius);
       }


    }

    @Test(expected = IllegalArgumentException.class)
    public void randomLocThrowsCorrectErrorForNullLocation() {
        RandomLocation.getRandomLocation(null, 10);

    }


}
