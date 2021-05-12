package sdp.moneyrun.weather;

import android.location.LocationManager;

import androidx.test.core.app.ActivityScenario;

import org.junit.Test;

import sdp.moneyrun.ui.weather.WeatherWidgetActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WeatherWidgetTest {

    @Test
    public void loadWeatherWorks() {
        try (ActivityScenario<WeatherWidgetActivity> scenario = ActivityScenario.launch(WeatherWidgetActivity.class)) {


            scenario.onActivity(a -> {
                android.location.Location location = new android.location.Location(LocationManager.PASSIVE_PROVIDER);
                location.setLatitude(40.741895);
                location.setLongitude(-73.989308);
                a.loadWeather(location);

            });

            Thread.sleep(5000);

            scenario.onActivity(a -> {
                assertNotNull(a.getCurrentForecast());
                assertNotNull(a.getCurrentLocation());

            });


        } catch (IllegalArgumentException | InterruptedException e) {
            assertEquals(1, 2);
            e.printStackTrace();
        }
    }

}
