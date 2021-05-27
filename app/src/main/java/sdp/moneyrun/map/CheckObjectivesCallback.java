package sdp.moneyrun.map;

import android.location.Location;

import androidx.annotation.NonNull;

import com.mapbox.mapboxsdk.location.LocationUpdate;

import java.lang.ref.WeakReference;

public interface CheckObjectivesCallback {

     default void updateLocation(@NonNull WeakReference<TrackedMap> weakReference, @NonNull Location location){
        TrackedMap trackedMap = weakReference.get();
        LocationUpdate.Builder locBuilder = new LocationUpdate.Builder();
        LocationUpdate locationUpdate =  locBuilder.location(location).build();
        trackedMap.getMapboxMap().getLocationComponent().forceLocationUpdate(locationUpdate);
        trackedMap.checkObjectives(location);
    }

}
