package sdp.moneyrun;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


/**
 * In this activity we do everything that needs to be done to update players info
 */
public class EndGameActivity extends AppCompatActivity {

    private ArrayList<Coin> collectedCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);
        collectedCoins = (ArrayList<Coin>) getIntent().getSerializableExtra("collectedCoins");
    }

    private void updatePlayerScore(){


    }

}