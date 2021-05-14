package sdp.moneyrun.ui;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import sdp.moneyrun.R;
import sdp.moneyrun.ui.authentication.LoginActivity;

public class MainActivity extends AppCompatActivity {
    public static boolean calledAlready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }
        new Handler().postDelayed(() -> {
            Intent authIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(authIntent);
            finish();
        }, 3000);
    }


    /**
     * When the MainActivity is destroyed i.e the app is closed the user is signed out
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
    }
}


