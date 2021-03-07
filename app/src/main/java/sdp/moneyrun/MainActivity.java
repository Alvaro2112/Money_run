package sdp.moneyrun;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    System.out.println("Permission granted.");
                } else {
                    System.out.println("Permission denied.");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final Button enableLocationPermissionButton = findViewById(R.id.enableLocationPermission);
        enableLocationPermissionButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED){
                    System.out.println("API that requires the permission can be used.");
                }else if(shouldShowRequestPermissionRationale()){
                    System.out.println("Explain to the user why your app requires this permission for a specific feature to behave as expected.");
                }else{
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
                }
            }
        });
    }

    private boolean shouldShowRequestPermissionRationale(){
        return false;
    }
}