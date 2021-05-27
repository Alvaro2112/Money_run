package sdp.moneyrun.location;

import android.annotation.SuppressLint;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;


public class MapLocationManager  {

    private LocationManager locationManager;

    public MapLocationManager(LocationManager locationManager, String provider){
        if(locationManager == null){
            throw new NullPointerException("location manager is null");
        }
        System.out.println(provider);
        if(provider == null ||provider.equals("test_provider") ){
            System.out.println(provider);
            locationManager.addTestProvider(provider,false,false,false,false,false,false,false, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
            locationManager.setTestProviderStatus(provider, LocationProvider.AVAILABLE,null,System.currentTimeMillis());
            Location mockedLocation = new Location("");
            mockedLocation.setLatitude(37.42);
            mockedLocation.setLongitude(-122.084);

            locationManager.setTestProviderLocation(provider,mockedLocation);
        }
        this.locationManager = locationManager;
    }
    @SuppressLint("MissingPermission")
    public void requestLocationUpdates(String provider, long minTimeMs, float minDistanceM, LocationListener listener ){
        locationManager.requestLocationUpdates(provider,minTimeMs,minDistanceM,listener);

    }
}
