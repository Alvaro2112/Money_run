package sdp.moneyrun.ui.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sdp.moneyrun.R;

public class SignUpActivity extends AppCompatActivity {
    private final String TAG = SignUpActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private boolean isClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_interface);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Until sign Out at destroy activity is implemented
        //    FirebaseAuth.getInstance().signOut();
        ///////////////////////////////////////////////////

        final Button submitButton = findViewById(R.id.signUpSubmitButton);
        submitButton.setOnClickListener(clicked -> {
            EditText emailView = findViewById(R.id.signUpEmailText);
            EditText passwordView = findViewById(R.id.signUpPassword);
            String email = emailView.getText().toString().trim();
            String password = passwordView.getText().toString().trim();
            isClicked = !isClicked;
            if (isClicked)
                submitButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            else
                submitButton.setBackgroundColor(getResources().getColor(R.color.design_default_color_background));
            if (checkInput(emailView, passwordView)) {
                submitSignUp(email, password);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //Check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    /**
     * This is needed for testing
     * Also, since it's one of the first activity created, it's reasonable to assume that when it's
     * destroyed the user should be signed out
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mAuth.signOut();
    }

    private void submitSignUp(@NonNull String email, @NonNull String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, task -> {
                    if (task.isSuccessful()) {
                        //Sign-In success
                        Log.d(TAG, "createUserWithEmail:success"); //Not sure about the tag thing
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Log.w(TAG, "CreateUserWithEmail:failure", task.getException());
                        Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(@Nullable FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, RegisterUserActivity.class);
            intent.putExtra("userId", user.getUid());
            startActivity(intent);
            finish();
        }
    }

    private boolean isPasswordValid(@NonNull CharSequence password) {
        return password.length() > 6;
    }

    private boolean isEmailValid(@NonNull CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean checkInput(@NonNull EditText emailView, @NonNull EditText passwordView) {
        boolean retValue = true;
        String email = emailView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();
        if (email.isEmpty()) {
            emailView.setError("Email is required");
            emailView.requestFocus();
            retValue = false;
        } else if (password.isEmpty()) {
            passwordView.setError("Password is required");
            passwordView.requestFocus();
            retValue = false;
        } else if (!isEmailValid(email)) {
            emailView.setError("Please enter a valid email address");
            emailView.requestFocus();
            retValue = false;
        } else if (!isPasswordValid(password)) {
            passwordView.setError("Password is too weak");
            passwordView.requestFocus();
            retValue = false;
        }
        return retValue;
    }


}