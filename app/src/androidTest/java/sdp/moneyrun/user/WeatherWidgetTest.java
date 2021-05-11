package sdp.moneyrun.user;

import android.os.Handler;

import androidx.test.core.app.ActivityScenario;

import org.junit.Test;

import java.util.ArrayList;

import sdp.moneyrun.ui.map.MapActivity;
import sdp.moneyrun.ui.weather.WeatherWidgetActivity;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WeatherWidgetTest {

    @Test
    public void onExplanationNeededWorks() {
        try (ActivityScenario<WeatherWidgetActivity> scenario = ActivityScenario.launch(WeatherWidgetActivity.class)) {

            Thread.sleep(5000);

                    scenario.onActivity(a -> {

                        assertNotNull(a.getCurrentForecast());
                        assertNotNull(a.getCurrentLocation());

                    });


        } catch (Exception e) {
            assertEquals(1, 2);
            e.printStackTrace();
        }
    }

}
