package sdp.moneyrun.map;

import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineResult;

import java.lang.ref.WeakReference;


public class LocationCheckObjectivesCallback implements LocationEngineCallback<LocationEngineResult> {

    private final WeakReference<MapActivity> activityWeakReference;

    LocationCheckObjectivesCallback(MapActivity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
    }



        /* Updates the location, then checks if near a coin and calls a  function accordingly
        */
    @Override
    public void onSuccess(LocationEngineResult result) {
        MapActivity activity = activityWeakReference.get();

        if (activity != null) {
            Location location = result.getLastLocation();
            if (location == null) {
                return;
            }
            // changed from the original one to have less getLastLocation !!
            // Pass the new location to the Maps SDK's LocationComponent
            if (activity.getMapboxMap() != null) {
                activity.getMapboxMap().getLocationComponent().forceLocationUpdate(location);
                activity.checkObjectives(location);
            }
        }
    }

    @Override
    public void onFailure(@NonNull Exception exception) {
        MapActivity activity = activityWeakReference.get();
        if (activity != null) {
            Toast.makeText(activity, exception.getLocalizedMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
