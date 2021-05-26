package sdp.moneyrun.map;

import android.annotation.SuppressLint;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.Arrays;
import java.util.List;

import sdp.moneyrun.R;


/*
 extend this map to have a map where the device is tracked
 */
@SuppressWarnings("FieldCanBeLocal")
public abstract class TrackedMap extends BaseMap implements
        PermissionsListener {
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private final List<String> INAPPROPRIATE_LOCATIONS = Arrays.asList("building", "motorway", "route cantonale", "sports_centre");
    public LocationEngine locationEngine;
    protected LocationCheckObjectivesCallback callback;
    private PermissionsManager permissionsManager;


    // source
    // https://docs.mapbox.com/android/maps/examples/location-change-listening/

    /**
     * //source : https://stackoverflow.com/questions/8832071/how-can-i-get-the-distance-between-two-point-by-latlng
     *
     * @param lat_a
     * @param lng_a
     * @param lat_b
     * @param lng_b
     * @return the distance in meters between two coordinates
     */
    public static double distance(double lat_a, double lng_a, double lat_b, double lng_b) {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b - lat_a);
        double lngDiff = Math.toRadians(lng_b - lng_a);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return distance * meterConversion;
    }

    /**
     * Initialize the Maps SDK's LocationComponent
     */
    @SuppressWarnings({"MissingPermission"})
    protected void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .useDefaultLocationEngine(false)
                            .build();

            locationComponent.activateLocationComponent(locationComponentActivationOptions);

            locationComponent.setLocationComponentEnabled(true);

            locationComponent.setCameraMode(CameraMode.TRACKING);

            locationComponent.setRenderMode(RenderMode.COMPASS);

            initLocationEngine();

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(this::enableLocationComponent);
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
        }
    }

    public void moveCameraTo(float latitude, float longitude) {
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .build();
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    public void moveCameraWithoutAnimation(double latitude, double longitude, double zoom) {
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(zoom)
                .build();
        mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
    }


    /**
     * @param location
     * @return the index of the closest coin whose distance is lower than a threshold or -1 if there are none
     */
    @Nullable
    public Coin nearestCoin(@NonNull Location location, @NonNull List<Coin> remainingCoins, double thresholdDistance) {

        double player_lat = location.getLatitude();
        double player_long = location.getLongitude();

        double min_dist = Integer.MAX_VALUE;
        double curr_dist;
        Coin min_coin = null;
        Coin curr_coin;

        for (int i = 0; i < remainingCoins.size(); ++i) {

            curr_coin = remainingCoins.get(i);

            curr_dist = distance(player_lat, player_long, curr_coin.getLatitude(), curr_coin.getLongitude());

            if (curr_dist < thresholdDistance && curr_dist < min_dist) {
                min_dist = curr_dist;
                min_coin = curr_coin;
            }
        }

        return min_coin;
    }

    public abstract void checkObjectives(Location location);
}
