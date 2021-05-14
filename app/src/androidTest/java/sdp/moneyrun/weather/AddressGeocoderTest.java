package sdp.moneyrun.weather;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;

import java.util.Locale;

import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.user.User;

import static org.junit.Assert.assertEquals;

public class AddressGeocoderTest {

    private Intent getStartIntent() {
        User currentUser = new User("999", "CURRENT_USER", "Epfl"
                , 0, 0, 0);
        Intent toStart = new Intent(ApplicationProvider.getApplicationContext(), MenuActivity.class);
        toStart.putExtra("user", currentUser);
        return toStart;
    }

    @Test
    public void getWeatherReportWorks() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
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
