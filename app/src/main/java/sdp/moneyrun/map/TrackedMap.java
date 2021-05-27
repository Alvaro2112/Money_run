package sdp.moneyrun.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.mapbox.android.core.location.LocationEngine;
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

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import sdp.moneyrun.R;


/*
 extend this map to have a map where the device is tracked
 */
@SuppressWarnings("FieldCanBeLocal")
public abstract class TrackedMap extends BaseMap implements
        PermissionsListener {
    public static final float DISTANCE_CHANGE_BEFORE_UPDATE = (float) 2;
    private static final long MINIMUM_TIME_BEFORE_UPDATE = 500;

    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private final List<String> INAPPROPRIATE_LOCATIONS = Arrays.asList("building", "motorway", "route cantonale", "sports_centre");
    public LocationEngine locationEngine;
    @Nullable
    private PermissionsManager permissionsManager;
    protected  MockLocationCheckObjectivesCallback mockLocationCheckObjectivesCallback;
    protected LocationCheckObjectivesCallback locationCheckObjectivesCallback;


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

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }

    }
    /**
     * Initializes the location manager and sets a callback so that checObjectives function is called
     *  everytime there is an update of the location
     * @param locationMode mode of location, by default is GPS_PROVIDER so that we don't use wifi
     */
    public void initLocationManager( String locationMode) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        MapLocationManager mapLocationManager = new MapLocationManager(new WeakReference<>(this),locationMode);
        mapLocationManager.setUpCallBack(mockLocationCheckObjectivesCallback,locationCheckObjectivesCallback);


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
    public MockLocationCheckObjectivesCallback getCallback(){
        return mockLocationCheckObjectivesCallback;
    }
    public abstract void checkObjectives(Location location);
}
