package sdp.moneyrun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class SignUpInterface extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_interface);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        final Button submitButton = (Button) findViewById(R.id.signUpSubmitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View clicked) {
                EditText emailView = (EditText) findViewById(R.id.signUpEmailText);
                EditText passwordView = (EditText)findViewById(R.id.signUpPassword);
                String email = emailView.getText().toString().trim();
                String password = passwordView.getText().toString().trim();
                if(checkInput()) {
                    submitSignUp(email, password);
                }
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        //Check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            updateUI(currentUser);
        }
    }

    private void submitSignUp(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpInterface.this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //Sign-In success
                            Log.d(MainActivity.TAG, "createUserWithEmail:success"); //Not sure about the tag thing
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else{
                            Log.w(MainActivity.TAG, "CreateUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpInterface.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void updateUI(FirebaseUser user) {
        if(user != null){
            // Intent intent = new Intent (MenuActivity.class);
            //  startActivity(intent);

            ////////////////////Awaiting Merge, Tests with MainActivity
            Intent intent = new Intent (this, MainActivity.class);
            //  startActivity(intent);
        }
    }
    private boolean isPasswordValid(CharSequence password){
        return password.length() > 6;
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

        private boolean checkInput(){
        boolean retValue = true;
        EditText emailView = (EditText) findViewById(R.id.signUpEmailText);
        EditText passwordView = (EditText)findViewById(R.id.signUpPassword);
        String email = emailView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();
        if(email.isEmpty()){
            emailView.setError("Email is required");
            emailView.requestFocus();
            retValue = false;
        }

        else if(password.isEmpty()){
            passwordView.setError("Password is required");
            passwordView.requestFocus();
            retValue = false;
        }

        else if(!isEmailValid(email)){
            emailView.setError("Please enter a valid email address");
            emailView.requestFocus();
            retValue = false;
        }

        else if(!isPasswordValid(password)){
            passwordView.setError("Password is too weak");
            passwordView.requestFocus();
            retValue = false;
        }
        return retValue;
    }


}