package sdp.moneyrun.ui.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import sdp.moneyrun.R;
import sdp.moneyrun.database.PlayerDatabaseProxy;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.menu.MenuActivity;


/**
 * In this activity we do everything that needs to be done to update players info
 */
public class EndGameActivity extends AppCompatActivity {

    private int score;
    private int numberOfCollectedCoins;
    private final int gameScore = 0;
    private TextView endText;
    private int playerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);
        endText = findViewById(R.id.end_game_text);
        numberOfCollectedCoins = (int) getIntent().getIntExtra("numberOfCollectedCoins", 0);
        score = (int) getIntent().getIntExtra("score", 0);
        playerId = getIntent().getIntExtra("playerId", 0);
        updateText(numberOfCollectedCoins, score, true);

        if (playerId != 0) {
            updatePlayer(playerId, gameScore);
        } else {
            updateText(-1, -1, false);
        }

        final ImageButton toMenu = (ImageButton) findViewById(R.id.end_game_button_to_menu);
        linkToMenuButton(toMenu);

    }

    /**
     * @param toMenu button to link
     *               Call this on the button to make start the Menu activity
     */

    private void linkToMenuButton(ImageButton toMenu){
        PlayerDatabaseProxy pdp = new PlayerDatabaseProxy();
        toMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdp.getPlayerTask(playerId).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()){
                            Player p = pdp.getPlayerFromTask(task);
                            Intent mainIntent = new Intent(EndGameActivity.this, MenuActivity.class);
                            mainIntent.putExtra("user", p);
                            startActivity(mainIntent);
                            finish();
                        }
                    }
                });

            }
        });

    }

    /**
     * @param numCoins  number of coins colelcted
     * @param gameScore score of the game (sum of values of coins)
     * @param succeeded (has managed to get the list of coins from the map activity
     *                  <p>
     *                  Update the Text view to display the player's score if succeeded
     *                  Else shows that it failed to get the score
     */
    public void updateText(int numCoins, int gameScore, boolean succeeded) {

        StringBuilder textBuilder = new StringBuilder();

        if (succeeded) {
            textBuilder = textBuilder.append("You have gathered").append(numCoins).append("coins");
            textBuilder = textBuilder.append("\n");
            textBuilder = textBuilder.append("For a total score of ").append(gameScore);
        } else {
            textBuilder = textBuilder.append("Unfortunately the coin you collected have been lost");
        }

        String newText = textBuilder.toString();
        endText.setText(newText);
    }


    /**
     * Adds the score of the game to the player total score
     *
     * @param playerId  player to update
     * @param gameScore score to be added
     */
    public Player updatePlayer(int playerId, int gameScore) {
        final Player player = new Player(playerId, "name", "address", 0, 0, gameScore);
        if (player != null) {
            player.setScore(gameScore, true);
        }
        return player;
    }

}