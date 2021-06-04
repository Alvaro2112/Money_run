package sdp.moneyrun.ui;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import sdp.moneyrun.R;
import sdp.moneyrun.ui.authentication.LoginActivity;

@SuppressWarnings("FieldCanBeLocal")
public class MainActivity extends AppCompatActivity {
    public static boolean calledAlready = false;
    //TIMES FOR THE ANIMATION
    private final int ANIMATION_START = 750;
    private final int ANIMATION_COIN_FADE = 2250;
    private final int ANIMATION_TITLE_FADE = 2700;
    private final int INTENT_START_ACTIVITY = 5400;
    private MediaPlayer mp;
    private Animation translateMan;
    private Animation alphaTitle;
    private ImageView runningMan;
    private ImageView coinImage;
    private ImageView appTitleImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);
        translateMan = AnimationUtils.loadAnimation(this, R.anim.translation_splash_screen);
        alphaTitle = AnimationUtils.loadAnimation(this, R.anim.alpha_title);
        mp = MediaPlayer.create(this, R.raw.splash_start);
        runningMan = findViewById(R.id.running_man);
        coinImage = findViewById(R.id.coin_image);
        appTitleImage = findViewById(R.id.app_title);
        translateMan.setFillAfter(true);
        alphaTitle.setFillAfter(true);

        startAnimations();
    }

    private void startAnimations() {
        new Handler().postDelayed(() -> runningMan.startAnimation(translateMan), ANIMATION_START);

        new Handler().postDelayed(() -> coinImage.setVisibility(View.INVISIBLE), ANIMATION_COIN_FADE);

        new Handler().postDelayed(() -> {
            appTitleImage.startAnimation(alphaTitle);
            mp.start();
        }, ANIMATION_TITLE_FADE);

        if (!calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }
        new Handler().postDelayed(() -> {
            Intent authIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(authIntent);
            finish();
        }, INTENT_START_ACTIVITY);
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


