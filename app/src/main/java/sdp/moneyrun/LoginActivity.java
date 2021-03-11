package sdp.moneyrun;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import sdp.moneyrun.permissions.PermissionsRequester;

public class LoginActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_login);
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

    // link from signup button to signup page
    public void signUp(View view) {
        Intent intent = new Intent(this, placeHolderSignUp.class);
        startActivity(intent);
    }

    public ActivityResultLauncher<String[]> getRequestPermissionsLauncher(){
        return requestPermissionsLauncher;
    }


}