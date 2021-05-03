package sdp.moneyrun.map;

import android.location.Location;

import com.google.firebase.database.FirebaseDatabase;

import org.junit.BeforeClass;
import org.junit.Test;

import sdp.moneyrun.ui.MainActivity;
import sdp.moneyrun.ui.map.MapActivity;

public class CoinGenerationHelperTest {

    @BeforeClass
    public static void setPersistence(){
        if(!MainActivity.calledAlready){
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
