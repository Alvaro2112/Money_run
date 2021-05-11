package sdp.moneyrun;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;

import org.junit.Test;

import java.io.IOException;

import sdp.moneyrun.map.LocationRepresentation;
import sdp.moneyrun.ui.map.MapActivity;
import sdp.moneyrun.ui.weather.WeatherWidgetActivity;
import sdp.moneyrun.weather.Address;
import sdp.moneyrun.weather.AddressGeocoder;

import static org.junit.Assert.assertEquals;

public class AddressGeocoderTest {


    @Test
    public void getWeatherReportWorks() {
        try (ActivityScenario<WeatherWidgetActivity> scenario = ActivityScenario.launch(WeatherWidgetActivity.class)) {
            scenario.onActivity(a-> {
                        AddressGeocoder addressGeocoder = AddressGeocoder.fromContext(a);
                        try {
                            Address address = addressGeocoder.getAddress(new LocationRepresentation(40.741895, -73.989308));
                            assertEquals(address.toString(),"193 5th Ave, New York, NY 10010, USA\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            );

        }
    }

}
