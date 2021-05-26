package sdp.moneyrun.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.Criteria;

import java.util.List;

public final class AndroidLocationService implements LocationService {

    private final LocationManager locationManager;
    private final String locationProvider;
    private final Criteria locationCriteria;

    private boolean isLocationMocked;
    private LocationRepresentation mockedLocation = null;

    private AndroidLocationService(LocationManager locationManager, String locationProvider, Criteria locationCriteria){
        this.locationManager = locationManager;
        this.locationProvider = locationProvider;
        this.locationCriteria = locationCriteria;
        this.isLocationMocked = false;
    }

    /**
     * @param context the activity context
     * @return a location manager from the given context.
     */
    private static LocationManager buildLocationManagerFromContext(Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * @param context  the context of the activity
     * @param locationProvider the location provider
     * @return a new lcation service with a fixed location provider
     */
    public static AndroidLocationService buildFromContextAndProvider(Context context, String locationProvider) {
        return new AndroidLocationService(
                buildLocationManagerFromContext(context),
                locationProvider,
                null
        );
    }

    /**
     * @param context  the context of the activity
     * @param locationCriteria the location criteria to choose which location provider to use
     * @return a new location service  with the best location provider
     */
    public static AndroidLocationService buildFromContextAndCriteria(Context context, Criteria locationCriteria) {
        return new AndroidLocationService(
                buildLocationManagerFromContext(context),
                null,
                locationCriteria
        );
    }

    /**
     * @return the location provider
     */
    public String getLocationProvider() {
        if (this.locationProvider != null) {
            return this.locationProvider;
        }

        return this.locationManager.getBestProvider(this.locationCriteria, true);
    }

    /**
     * @return the location criteria
     */
    public Criteria getLocationCriteria() {
        return this.locationCriteria;
    }

    /**
     * @return true if the location is mocked, false otherwise
     */
    public boolean isLocationMocked(){
        return this.isLocationMocked;
    }

    @Override
    public LocationRepresentation getCurrentLocation() {
        try{
            // Return mocked location if enabled
            if(this.isLocationMocked){
                return this.mockedLocation;
            }

            // Otherwise, look for best location getter
            Location location = getBestLocation();

            if(location == null){
                return null;
            }

            return new LocationRepresentation(location.getLatitude(), location.getLongitude());
        }catch (SecurityException e){
            throw e;
        }
    }

    /**
     * @return the best location provider.
     */
    private Location getBestLocation(){
        Location location = null;
        List<String> providers = locationManager.getProviders(true);

        for (String provider : providers) {
            @SuppressLint("MissingPermission")
            Location l = locationManager.getLastKnownLocation(provider);
            location = getUpdatedLocation(location, l);
        }
        return location;
    }

    /**
     * @param location a location
     * @param l another location
     * @return the best location between 2 locations.
     */
    public Location getUpdatedLocation(Location location, Location l){
        if(l == null){
            return location;
        }

        if (location == null || l.getAccuracy() < location.getAccuracy()) {
            return l;
        }
        return location;
    }

    /**
     * Define a mocked location.
     */
    public void setMockedLocation(LocationRepresentation locationRepresentation){
        if(locationRepresentation == null){
            throw new IllegalArgumentException("mocked location representation should not be null.");
        }
        this.isLocationMocked = true;
        this.mockedLocation = locationRepresentation;
    }

    /**
     * Remove mocked location and use default location.
     */
    public void resetMockedLocation(){
        this.isLocationMocked = false;
    }
}
