package sdp.moneyrun.menu;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

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

    /**
     *
     * @param view Current view before click
     * @param focusable Whether it can be dismissed by clicking outside the popup window
     * @param layoutId Id of the popup layout that will be used
     */
    public static PopupWindow onButtonShowPopupWindowClick(View view, Activity activity, Boolean focusable, int layoutId) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(layoutId, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window at wanted location
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        return popupWindow;
    }
}
