package sdp.moneyrun.map;

import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineResult;

import java.lang.ref.WeakReference;

public class LocationChangeListeningActivityLocationCallback implements LocationEngineCallback<LocationEngineResult> {

    private final WeakReference<TrackedMap> activityWeakReference;

    LocationChangeListeningActivityLocationCallback(TrackedMap activity) {
        this.activityWeakReference = new WeakReference<>(activity);
    }

    /**
     * The LocationEngineCallback interface's method which fires when the device's location has changed.
     *
     * @param result the LocationEngineResult object which has the last known location within it.
     */
    @Override
    public void onSuccess(LocationEngineResult result) {
        TrackedMap activity = activityWeakReference.get();

        if (activity != null) {
            Location location = result.getLastLocation();
            if (location == null) {
                return;
            }
            // changed from the original one to have less getLastLocation !!
            // Pass the new location to the Maps SDK's LocationComponent
            if (activity.getMapboxMap() != null) {
                activity.getMapboxMap().getLocationComponent().forceLocationUpdate(location);
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
        TrackedMap activity = activityWeakReference.get();
        if (activity != null) {
            Toast.makeText(activity, exception.getLocalizedMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
