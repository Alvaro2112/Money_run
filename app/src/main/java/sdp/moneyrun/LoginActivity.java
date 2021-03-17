package sdp.moneyrun;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import sdp.moneyrun.permissions.PermissionsRequester;

public class LoginActivity extends AppCompatActivity {

    private final ActivityResultLauncher<String[]> requestPermissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), map -> {
        for (String permission : map.keySet()) {
            boolean isGranted = map.get(permission);
            if (isGranted) {
                System.out.println("Permission" + permission + " granted.");
            } else {
                System.out.println("Permission" + permission + " denied.");
            }
        }
    });
    private final String coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;
    private final String fineLocation = Manifest.permission.ACCESS_FINE_LOCATION;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        PermissionsRequester locationPermissionsRequester = new PermissionsRequester(
                this,
                requestPermissionsLauncher,
                "In order to work properly, this app needs to use location services.",
                true,
                coarseLocation,
                fineLocation);
        locationPermissionsRequester.requestPermission();

        final Button loginButton = (Button) findViewById(R.id.loginButton);
        setLogIn(loginButton);

    }

    // link from signup button to signup page
    public void signUp(View view) {
        Intent intent = new Intent(this, placeHolderSignUp.class);
        startActivity(intent);
    }

    private void setLogIn(Button loginButton) {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View clicked) {
                EditText emailView = (EditText) findViewById(R.id.loginEmailAddress);
                EditText passwordView = (EditText) findViewById(R.id.loginPassword);
                String email = emailView.getText().toString().trim();
                String password = passwordView.getText().toString().trim();

                if (email.isEmpty()) {
                    emailView.setError("Email is required");
                    emailView.requestFocus();

                } else if (password.isEmpty()) {
                    passwordView.setError("Password is required");
                    passwordView.requestFocus();
                } else {
                    Intent menuIntent = new Intent(LoginActivity.this, MenuActivity.class);
                    startActivity(menuIntent);
                }
            }
        });

    }


    public ActivityResultLauncher<String[]> getRequestPermissionsLauncher() {
        return requestPermissionsLauncher;
    }


}