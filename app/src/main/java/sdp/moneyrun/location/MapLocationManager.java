package sdp.moneyrun.location;

import android.annotation.SuppressLint;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;


public class MapLocationManager  {

    private Location mockedLocation;
    private LocationManager locationManager;

    public MapLocationManager(LocationManager locationManager, String provider){
        if(provider == null ||provider.equals("test") ){
            Location mockedLocation = new Location("");//provider name is unnecessary
            mockedLocation.setLatitude(37.42);
            mockedLocation.setLongitude(-122.084);
            locationManager.addTestProvider("test_provider",false,false,false,false,false,false,false, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
            locationManager.setTestProviderLocation("test_provider",mockedLocation);
        }
    }
    @SuppressLint("MissingPermission")
    public void requestLocationUpdates(String provider, long minTimeMs, float minDistanceM, LocationListener listener ){
        locationManager.requestLocationUpdates(provider,minTimeMs,minDistanceM,listener);
    }
}
