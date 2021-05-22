package sdp.moneyrun.location;

import androidx.core.location.LocationManagerCompat;

import android.content.Context;
import android.location.LocationManager;
import android.location.Criteria;

public final class AndroidLocationService implements LocationService {

    private final LocationManager locationManager;
    private final String locationProvider;
    private final Criteria locationCriteria;

    AndroidLocationService(LocationManager locationManager, String locationProvider, Criteria locationCriteria){
        if(locationManager == null){
            throw new IllegalArgumentException("location manager should not be null.");
        }
        this.locationManager = locationManager;
        this.locationProvider = locationProvider;
        this.locationCriteria = locationCriteria;
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
    public static LocationService buildFromContextAndProvider(Context context, String locationProvider) {
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
    public static LocationService buildFromContextAndCriteria(Context context, Criteria locationCriteria) {
        return new AndroidLocationService(
                buildLocationManagerFromContext(context),
                null,
                locationCriteria
        );
    }

    private String getLocationProvider() {
        if (this.locationProvider != null) {
            return this.locationProvider;
        }

        return this.locationManager.getBestProvider(this.locationCriteria, true);
    }

    @Override
    public LocationRepresentation getCurrentLocation() {
        try{
            android.location.Location location = this.locationManager.getLastKnownLocation(this.getLocationProvider());

            if(location == null){
                return null;
            }

            return new LocationRepresentation(location.getLatitude(), location.getLongitude());
        }catch (SecurityException e){
            throw e;
        }
    }
}
