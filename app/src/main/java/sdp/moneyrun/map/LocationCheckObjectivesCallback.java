package sdp.moneyrun.map;

import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineResult;

import java.lang.ref.WeakReference;

import sdp.moneyrun.ui.map.MapActivity;
import sdp.moneyrun.ui.map.OfflineMapDownloaderActivity;


public class LocationCheckObjectivesCallback implements LocationEngineCallback<LocationEngineResult> {

    @NonNull
    private final WeakReference<TrackedMap> activityWeakReference;
    boolean init = false;
    //Whether we use the default MapBox location or if we use the GPS/Network location
    boolean useDefaultLocation = true;

    public LocationCheckObjectivesCallback(MapActivity activity, boolean useDefaultLocation) {
        this.activityWeakReference = new WeakReference<>(activity);
        this.useDefaultLocation = useDefaultLocation;
    }

    public LocationCheckObjectivesCallback(OfflineMapDownloaderActivity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
    }

    /**
     * If we use the GPS/Network location we only need to get the location from MapBox once at the start (We only need the getLastLocation to start)
     * and thus we will never update again using MapBox
     */
    public boolean hasToUpdate() {
        TrackedMap activity = activityWeakReference.get();
        boolean hasToUpdate = !init || useDefaultLocation;
        return activity != null && hasToUpdate;
    }

    /* Updates the location, then checks if near a coin and calls a  function accordingly
     */
    @Override
    public void onSuccess(@NonNull LocationEngineResult result) {
        TrackedMap activity = activityWeakReference.get();

        if (hasToUpdate()) {
            Location location = result.getLastLocation();
            // Pass the new location to the Maps SDK's LocationComponent
            if (activity.getMapboxMap() != null && location != null) {

                activity.getMapboxMap().getLocationComponent().forceLocationUpdate(location);
                activity.checkObjectives(location);
                init = true; //We got the first location, if useDefaultLocation is false we will never enter this if statement again.
            }
        }
    }

    @Override
    public void onFailure(@NonNull Exception exception) {
        TrackedMap activity = activityWeakReference.get();
        if (activity != null) {
            Toast.makeText(activity, exception.getLocalizedMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }


}
