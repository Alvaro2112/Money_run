package sdp.moneyrun.map;

import static android.content.Context.LOCATION_SERVICE;
import static android.os.Looper.getMainLooper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;

import java.lang.ref.WeakReference;

public class MapLocationManager {
    public static final double THRESHOLD_DISTANCE = 5;
    public static final float DISTANCE_CHANGE_BEFORE_UPDATE = (float) 2;
    private static final double ZOOM_FOR_FEATURES = 15.;
    private static final long MINIMUM_TIME_BEFORE_UPDATE = 1000;
    private static final long MAXIMUM_TIME_BEFORE_UPDATE = 2 * MINIMUM_TIME_BEFORE_UPDATE;

    @NonNull
    private final WeakReference<TrackedMap> activityWeakReference;
    private String locationMode;
    private boolean isMock;
    private LocationManager locationManager;

    public MapLocationManager(@NonNull WeakReference<TrackedMap> weakReference, String locationMode) {
        activityWeakReference = weakReference;
        this.locationMode = locationMode;

        if (locationMode == null || locationMode.equals("test_provider")) {
            isMock = true;
        } else {
            locationManager = (LocationManager) weakReference.get().getSystemService(LOCATION_SERVICE);

            isMock = false;
        }
    }

    public void setUpCallBack(MockLocationCheckObjectivesCallback mockCallback, LocationCheckObjectivesCallback callback) {
        if (ActivityCompat.checkSelfPermission(activityWeakReference.get(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activityWeakReference.get(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (isMock) {
            LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(activityWeakReference.get());

            LocationEngineRequest request = new LocationEngineRequest.Builder(MINIMUM_TIME_BEFORE_UPDATE)
                    .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                    .setMaxWaitTime(MAXIMUM_TIME_BEFORE_UPDATE).build();
            locationEngine.requestLocationUpdates(request, mockCallback, getMainLooper());
            locationEngine.getLastLocation(mockCallback);

        }
        else{
            locationManager.requestLocationUpdates(locationMode, MINIMUM_TIME_BEFORE_UPDATE, DISTANCE_CHANGE_BEFORE_UPDATE, callback);
        }
    }

}
