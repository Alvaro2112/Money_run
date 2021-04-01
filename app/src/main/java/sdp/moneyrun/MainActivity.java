package sdp.moneyrun;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    public final static String TAG = "MainActivity";
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent authIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(authIntent);
                finish();
            }
        }, 3000);
    }


    /**
     * When the MainActivity is destroyed i.e the app is closed the user is signed out
     */
    @Override
    protected  void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
    }
}


