package sdp.moneyrun.ui.weather;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;

import sdp.moneyrun.map.LocationRepresentation;
import sdp.moneyrun.weather.Address;
import sdp.moneyrun.weather.AddressGeocoder;
import sdp.moneyrun.weather.OpenWeatherMap;
import sdp.moneyrun.weather.WeatherForecast;


public class WeatherWidgetActivity extends AppCompatActivity {
    public static final float DISTANCE_CHANGE_BEFORE_UPDATE = (float) 0.00001;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final long MINIMUM_TIME_BEFORE_UPDATE = 10000;
    private OpenWeatherMap openWeatherMap;
    private AddressGeocoder addressGeocoder;
    private WeatherForecast currentForecast;
    private LocationRepresentation currentLocation;

    LocationListener locationListenerGPS = this::loadWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MINIMUM_TIME_BEFORE_UPDATE, DISTANCE_CHANGE_BEFORE_UPDATE, locationListenerGPS);
        openWeatherMap = OpenWeatherMap.build();
        addressGeocoder = AddressGeocoder.fromContext(this);

    }

    private void loadWeather(android.location.Location location) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
            return;
        }

        try {
            LocationRepresentation loc;
            loc = new LocationRepresentation(location.getLatitude(), location.getLongitude());
            this.currentLocation = loc;
            System.out.println(loc.getLatitude());
            this.currentForecast = openWeatherMap.getForecast(loc);

            Address address = addressGeocoder.getAddress(loc);

        } catch (IOException e) {
            Log.e("WeatherActivity", "Error when retrieving forecast.", e);
        }
    }

    public WeatherForecast getCurrentForecast() {
        return this.currentForecast;
    }


    public LocationRepresentation getCurrentLocation() {
        return this.currentLocation;
    }
}