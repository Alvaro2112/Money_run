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
    public final String INPUT_ERROR_MESSAGE = "Email and/or password is blank or invalid";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_interface);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        setErrorMessage();
        setErrorVisibility(View.INVISIBLE);
        Button submitButton = (Button) findViewById(R.id.signUpSubmitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View clicked) {
                String email = SignUpInterface.this.findViewById(R.id.signUpEmailText).toString();
                String password = SignUpInterface.this.findViewById(R.id.signUpPassword).toString();
                if (email != null && password != null && SignUpInterface.this.isEmailValid(email)) {
                    setErrorVisibility(View.INVISIBLE);
                    SignUpInterface.this.submitSignUp(email, password);
                } else {
                    setErrorVisibility(View.VISIBLE);
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
                            updateUI(null);
                        }
                    }
                });
    }



    private void updateUI(FirebaseUser user) {
        if(user != null){
            // Intent intent = new Intent (MenuActivity.class);
            //  startActivity(intent);
        }
    }


    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void setErrorMessage(){
        TextView errorView = (TextView) findViewById(R.id.SignUpErorMessage);
        errorView.setText(INPUT_ERROR_MESSAGE);
    }

    private void setErrorVisibility(int visibility){
        TextView errorView = (TextView) findViewById(R.id.SignUpErorMessage);
        errorView.setVisibility(visibility);
    }

}