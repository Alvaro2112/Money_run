package sdp.moneyrun.map;

import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineResult;

import java.lang.ref.WeakReference;

import sdp.moneyrun.ui.map.MapActivity;
import sdp.moneyrun.ui.map.OfflineMapDownloaderActivity;

public class MockLocationCheckObjectivesCallback implements LocationEngineCallback<LocationEngineResult>,CheckObjectivesCallback {

    @NonNull
    private final WeakReference<TrackedMap> activityWeakReference;


    public MockLocationCheckObjectivesCallback(MapActivity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
    }

    public MockLocationCheckObjectivesCallback(OfflineMapDownloaderActivity activity) {

        this.activityWeakReference = new WeakReference<>(activity);
    }

    /* Updates the location, then checks if near a coin and calls a  function accordingly
     */
    @Override
    public void onSuccess(@NonNull LocationEngineResult result) {
            Location location = result.getLastLocation();
            updateLocation(activityWeakReference,location);
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
