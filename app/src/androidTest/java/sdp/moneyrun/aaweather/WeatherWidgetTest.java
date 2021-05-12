package sdp.moneyrun.aaweather;

import android.location.LocationManager;

import androidx.test.core.app.ActivityScenario;

import org.junit.Test;

import sdp.moneyrun.ui.weather.WeatherWidgetActivity;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WeatherWidgetTest {

    @Test
    public void onExplanationNeededWorks() {
        try (ActivityScenario<WeatherWidgetActivity> scenario = ActivityScenario.launch(WeatherWidgetActivity.class)) {


                    scenario.onActivity(a -> {
                        android.location.Location location = new android.location.Location(LocationManager.PASSIVE_PROVIDER);

                    });



        } catch (Exception e) {
            assertEquals(1, 2);
            e.printStackTrace();
        }
    }

}
