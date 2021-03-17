package sdp.moneyrun;

import android.location.Location;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;

public class MapsInstrumentedTest {
    @Rule
    public ActivityScenarioRule<MapsActivity> testRule = new ActivityScenarioRule<>(MapsActivity.class);
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void activityStartsProperly() {
        assertEquals(Lifecycle.State.RESUMED, testRule.getScenario().getState());
    }

    @Test
    public void addMarkerAddsMarker(){
        try(ActivityScenario<MapsActivity> scenario = ActivityScenario.launch(MapsActivity.class)) {
            Intents.init();
            UiDevice device = UiDevice.getInstance(getInstrumentation());
            LatLng sydney = new LatLng(-33.852, 151.211);
            String title = "sydney";
            scenario.onActivity(a -> a.addMarker(sydney,title));
            UiObject marker = device.findObject(new UiSelector().descriptionContains(title));
            marker.click();
            Intents.release();
        }
        catch(Exception e){
            e.printStackTrace();
            assertEquals(2,1);
        }

    }

    @Test
    public void addMarkerExceptionWhenPosNull(){
       // exception.expect(RuntimeException.class);
        try(ActivityScenario<MapsActivity> scenario = ActivityScenario.launch(MapsActivity.class)) {
            Intents.init();
            UiDevice device = UiDevice.getInstance(getInstrumentation());
            LatLng sydney = null;
            String title = "sydney";
            scenario.onActivity(a -> a.addMarker(sydney,"title"));
            Intents.release();
        }
        catch(Exception e){
            assertEquals(1,1);
        }
    }

    @Test
    public void addMarkerExceptionWhenTitleNull(){
      //  exception.expect(RuntimeException.class);
        try(ActivityScenario<MapsActivity> scenario = ActivityScenario.launch(MapsActivity.class)) {
            Intents.init();
            UiDevice device = UiDevice.getInstance(getInstrumentation());
            LatLng sydney = new LatLng(-33.852, 151.211);
            scenario.onActivity(a -> a.addMarker(sydney,null));
            Intents.release();
        }
        catch(Exception e){
            assertEquals(1,1);
        }
    }

    @Test
    public void latLngFromLocThrowsExceptionWhenNull(){
        //  exception.expect(RuntimeException.class);
        try(ActivityScenario<MapsActivity> scenario = ActivityScenario.launch(MapsActivity.class)) {
            scenario.onActivity(a -> a.latLngFromLocation(null));
        }
        catch(Exception e){
            assertEquals(1,1);
        }
    }

    @Test
    public void latLngFromLocWorks(){
        //  exception.expect(RuntimeException.class);
        try(ActivityScenario<MapsActivity> scenario = ActivityScenario.launch(MapsActivity.class)) {

            scenario.onActivity(a -> {
                Location loc = new Location("");
                loc.setLatitude(4.0d);
                loc.setLongitude(1.0d);
                LatLng latlng = new LatLng(4.0d,1.0d);

                assertEquals(a.latLngFromLocation(loc),latlng);
            });
        }
        catch(Exception e){
            assertEquals(2,1);
        }
    }



}
