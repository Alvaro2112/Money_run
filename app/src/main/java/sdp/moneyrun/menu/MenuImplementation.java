package sdp.moneyrun.menu;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import sdp.moneyrun.R;
import sdp.moneyrun.permissions.PermissionsRequester;

public class MenuImplementation {
    /**
     * Requests location permissions to the user
     * @param activity the current activity
     * @param requestPermissionsLauncher the permission requester
     */
    public static void requestLocationPermissions(AppCompatActivity activity, ActivityResultLauncher<String[]> requestPermissionsLauncher){

        if (
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            String coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;
            String fineLocation = Manifest.permission.ACCESS_FINE_LOCATION;

            PermissionsRequester locationPermissionsRequester = new PermissionsRequester(
                    activity,
                    requestPermissionsLauncher,
                    activity.getString(R.string.user_location_permission_explanation),
                    false,
                    coarseLocation,
                    fineLocation);
            locationPermissionsRequester.requestPermission();
        }
    }
}
