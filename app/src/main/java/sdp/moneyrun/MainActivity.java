package sdp.moneyrun;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.widget.Button;

import sdp.moneyrun.permissions.PermissionsRequester;

public class MainActivity extends AppCompatActivity {

    private final ActivityResultLauncher<String[]> requestPermissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), map -> {
        for(String permission : map.keySet()){
            boolean isGranted = map.get(permission);
            if (isGranted) {
                System.out.println("Permission" + permission + " granted.");
            } else {
                System.out.println("Permission" + permission + " denied.");
            }
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        String coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;
        String fineLocation = Manifest.permission.ACCESS_FINE_LOCATION;
        PermissionsRequester locationPermissionsRequester = new PermissionsRequester(
                this,
                requestPermissionsLauncher,
                "In order to work properly, this app needs to use location services.",
                true,
                coarseLocation,
                fineLocation);
        locationPermissionsRequester.requestPermission();
    }

    public ActivityResultLauncher<String[]> getRequestPermissionsLauncher(){
        return requestPermissionsLauncher;
    }
}