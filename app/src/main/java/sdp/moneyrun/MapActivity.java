package sdp.moneyrun;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;

import java.lang.ref.WeakReference;
import java.util.List;

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback, PermissionsListener {
    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    private static final float ZOOM = 4;
    private static final int GAME_TIME = 100;
    private static int chronometerCounter =0;
    private MapView mapView;
    private SymbolManager symbolManager;
    private MapboxMap mapboxMap;
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationChangeListeningActivityLocationCallback callback =
            new LocationChangeListeningActivityLocationCallback(this);

    private Chronometer chronometer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_map);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        initChronometer();
    }

    /**
     * @param mapboxMap the map where everything will be done
    *     this overried the OnMapReadyCallback in the implemented interface
   *      We set up the symbol manager here, it will allow us to add markers and other visual stuff on the map
     *                    *       Then we setup the location tracking
     */
    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {

        mapboxMap.setStyle(Style.MAPBOX_STREETS,style -> {
            GeoJsonOptions geoJsonOptions = new GeoJsonOptions().withTolerance(0.4f);
            symbolManager =  new SymbolManager(mapView, mapboxMap, style, null, geoJsonOptions);
            symbolManager.setIconAllowOverlap(true);
            symbolManager.setTextAllowOverlap(true);
            enableLocationComponent(style);
                });

        this.mapboxMap = mapboxMap;
    }


    /**
     * The chronometer will countdown from the maximum time of a game to 0
     */
    private void initChronometer(){
        chronometer = (Chronometer) findViewById(R.id.mapChronometer);
        chronometer.start();
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if(chronometerCounter < GAME_TIME){
                    chronometerCounter += 1;
                }
                else{
                    displayEndOfTimer();
                }
                chronometer.setFormat("REMAINING TIME"+String.valueOf(GAME_TIME - chronometerCounter));
            }
        });
    }
    public Chronometer getChronometer(){
        return chronometer;
    }


    private void displayEndOfTimer(){
        //TODO
        // fill this function
    }
    public void addMarker(float latitude,float longitude){
        LatLng latLng = new LatLng(latitude,longitude);
        symbolManager.create(new SymbolOptions().withLatLng(latLng));
    }

    public SymbolManager getSymbolManager(){
        return symbolManager;
    }

    public MapboxMap getMapboxMap(){
        return mapboxMap;
    }

    public void moveCameraTo(float latitude, float longitude){
        LatLng latLng = new LatLng(latitude,longitude);
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .build();
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }


    // source
    // https://docs.mapbox.com/android/maps/examples/location-change-listening/
    /**
     * Initialize the Maps SDK's LocationComponent
     */
    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
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
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
        }
    }
    private static class LocationChangeListeningActivityLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MapActivity> activityWeakReference;

        LocationChangeListeningActivityLocationCallback(MapActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {
            MapActivity activity = activityWeakReference.get();

            if (activity != null) {
                Location location = result.getLastLocation();
                if (location == null) {
                    return;
                }
                // Pass the new location to the Maps SDK's LocationComponent
                if (activity.mapboxMap != null ) {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(location);
                }
            }
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location can't be captured
         *
         * @param exception the exception message
         */
        @Override
        public void onFailure(@NonNull Exception exception) {
            MapActivity activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

}