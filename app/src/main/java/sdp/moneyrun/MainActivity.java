package sdp.moneyrun;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        /*When the app starts, if the user is already logged in, keeps going, O.W sends him
          to the authentication activity*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(user != null){
                    Intent dashBoardIntent = new Intent(getApplicationContext(), Dashboard.class);
                    startActivity(dashBoardIntent);
                    finish();
                }
                else{
                    Intent signInIntent = new Intent(getApplicationContext(), Authentication.class);
                    startActivity(signInIntent);
                    finish();
                }
            }
        }, 3000);

    }
}