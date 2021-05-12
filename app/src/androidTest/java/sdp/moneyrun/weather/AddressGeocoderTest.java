package sdp.moneyrun.weather;

import androidx.test.core.app.ActivityScenario;

import org.junit.Test;

import java.util.Locale;

import sdp.moneyrun.ui.weather.WeatherWidgetActivity;

import static org.junit.Assert.assertEquals;

public class AddressGeocoderTest {


    @Test
    public void getWeatherReportWorks() {
        try (ActivityScenario<WeatherWidgetActivity> scenario = ActivityScenario.launch(WeatherWidgetActivity.class)) {
            scenario.onActivity(a -> {
                        AddressGeocoder addressGeocoder = AddressGeocoder.fromContext(a);
                        Locale locale = new Locale("French");
                        android.location.Address address = new android.location.Address(locale);
                        address.setAddressLine(0, "193 5th Ave, New York, NY 10010, USA");
                        Address addr = addressGeocoder.convertToAddress(address);
                        assertEquals(addr.toString(), "193 5th Ave, New York, NY 10010, USA\n");
                    }
            );

        }
    }

}
