package sdp.moneyrun.map;

import android.location.Location;
import android.location.LocationListener;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

import sdp.moneyrun.ui.map.MapActivity;
import sdp.moneyrun.ui.map.OfflineMapDownloaderActivity;

public class LocationCheckObjectivesCallback   implements LocationListener,CheckObjectivesCallback {
    @NonNull
    private final WeakReference<TrackedMap> activityWeakReference;

    public LocationCheckObjectivesCallback(@NonNull MapActivity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
    }
    public LocationCheckObjectivesCallback(@NonNull OfflineMapDownloaderActivity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        updateLocation(activityWeakReference,location);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

}
