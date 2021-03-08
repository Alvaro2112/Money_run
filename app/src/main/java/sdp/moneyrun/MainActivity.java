package sdp.moneyrun;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import sdp.moneyrun.permission.RequestPermissions;

public class MainActivity extends AppCompatActivity {

    private final String coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;
    private final String fineLocation = Manifest.permission.ACCESS_FINE_LOCATION;
    private final RequestPermissions requestLocationPermissions = new RequestPermissions(
            this,
            "In order to function properly, this app needs to use location services.",
            true,
            coarseLocation,
            fineLocation);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final Button enableLocationPermissionButton = findViewById(R.id.enableLocationPermission);
        enableLocationPermissionButton.setOnClickListener(v -> requestLocationPermissions.requestPermission());
    }
}