package sdp.moneyrun;

import android.location.Location;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;

import org.junit.Test;

import java.util.ArrayList;

import sdp.moneyrun.map.MapActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MapInstrumentedTest {



    @Test
    public void moveCameraToWorks() {
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            float lat = 8f;
            float lon = 8f;
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a ->{
                a.moveCameraTo(lat,lon);
            });
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a ->{
                LatLng latLng = a.getMapboxMap().getCameraPosition().target;
                assertEquals(latLng.getLatitude(), 8.0,0.1);
                assertEquals(latLng.getLongitude(), 8.0,0.1);
                System.out.println("LONGITUDE IS "+String.valueOf(latLng.getLatitude()));
            });
        }
        catch (Exception e){
            assertEquals(-1,2);
            e.printStackTrace();
        }
    }

    @Test
    public void testSymbolManager() {
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            scenario.onActivity(a->{
               assertEquals( a.getSymbolManager().getIconAllowOverlap(),true);
                assertEquals( a.getSymbolManager().getTextAllowOverlap(),true);

            });
        }
        catch (Exception e){
            assertEquals(-1,2);
            e.printStackTrace();
        }
    }

    @Test
    public void locationTracking() {
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            scenario.onActivity(a->{
                assertEquals(a.getMapboxMap().getLocationComponent().getCameraMode(), CameraMode.TRACKING) ;
                assertEquals(a.getMapboxMap().getLocationComponent().getRenderMode(), RenderMode.COMPASS);
            });
        }
        catch (Exception e){
            assertEquals(-1,2);
            e.printStackTrace();
        }
    }




    @Test
    public void chronometerTest() {
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            scenario.onActivity(a->{
                assertFalse(a.getChronometer().isCountDown());
                assertTrue(a.getChronometer().getText().toString().contains("REMAINING TIME"));
            });
        }
        catch (Exception e){
            assertEquals(-1,2);
            e.printStackTrace();
        }
    }

    @Test
    public void onExplanationNeededWorks() {
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ArrayList<String> reasons = new ArrayList<>();
            reasons.add("e");
            scenario.onActivity(a->{
                a.onExplanationNeeded(reasons);
            });
            assertEquals(1,1);
        }
        catch (Exception e){
            assertEquals(-1,2);
            e.printStackTrace();
        }
    }

    @Test
    public void onPermissionResultWorks() {
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean granted = true;
            scenario.onActivity(a->{
                a.onPermissionResult(granted);
            });
            assertEquals(1,1);
        }
        catch (Exception e){
            assertEquals(-1,2);
            e.printStackTrace();
        }
    }

    @Test
    public void addCoinAddsCoinToMap() {
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a->{
                Location curloc = a.getCurrentLocation();
                Coin coin = new Coin(curloc.getLatitude()/2,curloc.getLongitude()/2);
                a.addCoin(coin);
                Coin coin2 = new Coin(curloc.getLatitude()/3,curloc.getLongitude()/100);
                a.addCoin(coin2);
            });
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            scenario.onActivity(a->{
                assertEquals(2,a.getSymbolManager().getAnnotations().size());
            });

        }
        catch (Exception e){
            assertEquals(-1,2);
            e.printStackTrace();
        }
    }


    @Test
    public void removeCoinRemovesCoinFromMap() {
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a->{
                Location curloc = a.getCurrentLocation();
                Coin coin = new Coin(curloc.getLatitude()/2,curloc.getLongitude()/2);
                a.addCoin(coin);
                Coin coin2 = new Coin(curloc.getLatitude()/3,curloc.getLongitude()/100);
                a.addCoin(coin2);
                a.removeCoin(coin);
            });
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            scenario.onActivity(a->{
                assertEquals(1,a.getSymbolManager().getAnnotations().size());
            });

        }
        catch (Exception e){
            assertEquals(-1,2);
            e.printStackTrace();
        }
    }


    @Test
    public void catchCoinWhenNearRemovesAndAddsFromList() {
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a->{
                Location curloc = a.getCurrentLocation();
                Coin coin = new Coin(curloc.getLatitude(),curloc.getLongitude());
                a.addCoin(coin);
                Coin coin2 = new Coin(curloc.getLatitude()/3,curloc.getLongitude()/100);
                a.addCoin(coin2);
            });
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            scenario.onActivity(a->{
                assertEquals(1,a.getRemainingCoins().size());
                assertEquals(1,a.getCollectedCoins().size());
            });

        }
        catch (Exception e){
            assertEquals(-1,2);
            e.printStackTrace();
        }
    }

    @Test
    public void catchCoinWhenNearRemovesFromMap() {
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a->{
                Location curloc = a.getCurrentLocation();
                Coin coin = new Coin(curloc.getLatitude(),curloc.getLongitude());
                a.addCoin(coin);
            });
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            scenario.onActivity(a->{
                assertEquals(0,a.getSymbolManager().getAnnotations().size());
            });

        }
        catch (Exception e){
            e.printStackTrace();
            assertEquals(-1,2);
        }
    }

    @Test
    public void QuestionsPopsUpWhenCoinIsCollected() {
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a->{
                Location curloc = a.getCurrentLocation();
                Coin coin = new Coin(curloc.getLatitude(),curloc.getLongitude());
                a.addCoin(coin);
            });
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            onView(ViewMatchers.withId(R.id.ask_question_popup)).check(matches(isDisplayed()));

        }
        catch (Exception e){
            assertEquals(-1,2);
            e.printStackTrace();
        }
    }


}
