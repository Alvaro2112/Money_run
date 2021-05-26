package sdp.moneyrun.ui.authentication;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import sdp.moneyrun.R;
import sdp.moneyrun.database.DatabaseProxy;
import sdp.moneyrun.database.UserDatabaseProxy;
import sdp.moneyrun.permissions.PermissionsRequester;
import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.user.User;

@SuppressWarnings("FieldCanBeLocal")
public class LoginActivity extends AppCompatActivity {

    private final String TAG = LoginActivity.class.getSimpleName();
    private final ActivityResultLauncher<String[]> requestPermissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), map -> {
    });
    private final String coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;
    private final String fineLocation = Manifest.permission.ACCESS_FINE_LOCATION;
    private final String ERROR_MISSING_EMAIL = "Email is required";
    private final String ERROR_MISSING_PASSWORD = "Password is required";
    private final String ERROR_INVALID_EMAIL_FORMAT = "Email format is invalid";
    private FirebaseAuth mAuth;

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

        mAuth = FirebaseAuth.getInstance();

        final Button loginButton = findViewById(R.id.loginButton);
        setLogIn(loginButton);

        final Button guestButton = findViewById(R.id.guestButton);
        setGuestButton(guestButton);
        DatabaseProxy.addOfflineListener(this, TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DatabaseProxy.removeOfflineListener();
    }
    @Override
    protected void onResume() {
        super.onResume();
        DatabaseProxy.addOfflineListener(this, TAG);
    }

    protected void onStop(){
        super.onStop();
        DatabaseProxy.removeOfflineListener();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    // link from signUp button to signUp page
    public void signUp(View view) {
        MediaPlayer.create(this, R.raw.button_press).start();
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    private void setLogIn(@NonNull Button loginButton) {
        loginButton.setOnClickListener(clicked -> {
            MediaPlayer.create(this, R.raw.button_press).start();
            EditText emailView = findViewById(R.id.loginEmailAddress);
            EditText passwordView = findViewById(R.id.loginPassword);
            String email = emailView.getText().toString().trim();
            String password = passwordView.getText().toString().trim();

            if (email.isEmpty()) {
                emailView.setError(ERROR_MISSING_EMAIL);
                emailView.requestFocus();

            } else if (password.isEmpty()) {
                passwordView.setError(ERROR_MISSING_PASSWORD);
                passwordView.requestFocus();
            } else if (!isEmailValid(email)) {
                emailView.setError(ERROR_INVALID_EMAIL_FORMAT);
                emailView.requestFocus();
            } else {
                submitLogin(email, password);
            }
        });

    }

    private void submitLogin(@NonNull String email, @NonNull String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        String errorMessage = task.getException().getMessage() == null
                                ? "Reason unknown"
                                : task.getException().getMessage();
                        Toast.makeText(
                                LoginActivity.this,
                                "Authentication failed: " + errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(@Nullable FirebaseUser user) {
        if (user != null) {
            Intent menuIntent = new Intent(LoginActivity.this, MenuActivity.class);
            getUserFromDB(user.getUid(), menuIntent);
        }
    }

    private void getUserFromDB(@NonNull String userId, @NonNull Intent menuIntent) {
        UserDatabaseProxy pdb = new UserDatabaseProxy();
        Task<DataSnapshot> t = pdb.getUserTask(userId);
        t.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = pdb.getUserFromTask(task);

                // If no user has been found, we need to create a new instance in the database
                if (user == null) {
                    Intent intent = new Intent(this, RegisterUserActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }

                // Otherwise, the user exists and we go to the menu
                else {
                    menuIntent.putExtra("user", user);
                    startActivity(menuIntent);
                }
                finish();
            }
        });
    }

    private boolean isEmailValid(@NonNull CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @NonNull
    public ActivityResultLauncher<String[]> getRequestPermissionsLauncher() {
        return requestPermissionsLauncher;
    }

    /**
     * Should transfer the user to register activity so it can create a temporary
     * profile used only in this session of login
     *
     * @param guestButton offline mode option
     */
    public void setGuestButton(@Nullable Button guestButton) {
        if (guestButton == null)
            throw new IllegalArgumentException("Guest button was clicked but was null");
        guestButton.setOnClickListener(v -> {
            MediaPlayer.create(this, R.raw.button_press).start();
            Intent guestMenuIntent = new Intent(LoginActivity.this, RegisterUserActivity.class);
            guestMenuIntent.putExtra("guestUser", true);
            startActivity(guestMenuIntent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
    }

}