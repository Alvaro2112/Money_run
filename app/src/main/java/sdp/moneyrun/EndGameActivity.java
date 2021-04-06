package sdp.moneyrun;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EndGameActivity extends AppCompatActivity {

    private ArrayList<Coin> collectedCoins;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);
        collectedCoins = (ArrayList<Coin>) getIntent().getSerializableExtra("collectedCoins");
    }


}