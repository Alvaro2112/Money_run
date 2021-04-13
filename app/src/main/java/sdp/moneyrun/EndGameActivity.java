package sdp.moneyrun;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


/**
 * In this activity we do everything that needs to be done to update players info
 */
public class EndGameActivity extends AppCompatActivity {

    private ArrayList<Integer> collectedCoinsValues;
    private int gameScore = 0;
    private TextView endText;
    private int playerId;
    @Override
    protected void onCreate(Bundle  savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);
        endText = findViewById(R.id.end_game_text);
        collectedCoinsValues = (ArrayList<Integer>) getIntent().getSerializableExtra("collectedCoins");
        playerId = getIntent().getIntExtra("playerId",0);
        if(collectedCoinsValues != null){
            gameScore = getTotalScore();
            updateText(collectedCoinsValues.size(),gameScore,true);
            if(playerId != 0) {
                updatePlayer(playerId, gameScore);
            }
        }
        else{
            updateText(-1,-1,false);
        }
        final Button toMenu = (Button) findViewById(R.id.end_game_button_to_menu);
        linkToMenuButton(toMenu);

    }

    private void linkToMenuButton(Button toMenu){
        toMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(EndGameActivity.this, MenuActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });

    }
    private int getTotalScore(){
        int totalScore = 0;
        for(int val: collectedCoinsValues){
            totalScore += val;
        }
        return totalScore;
    }
    public void updateText(int numCoins, int gameScore,boolean succeeded){
        StringBuilder textBuilder = new StringBuilder();

        if(succeeded){
        textBuilder = textBuilder.append("You have gathered").append(numCoins).append("coins");
        textBuilder = textBuilder.append("\n");
        textBuilder = textBuilder.append("For a total score of ").append(gameScore);
        }
        else{
            textBuilder = textBuilder.append("Unfortunately the coin you collected have been lost");
        }
        String newText = textBuilder.toString();
        endText.setText(newText);
    }


    /**
     * Adds the score of the game to the player total score
     * @param playerId player to update
     * @param gameScore score to be added
     */
    public Player updatePlayer(int playerId, int gameScore){
        final Player player = new Player(playerId, "name", "adresd", 0, 0,gameScore);
        if(player != null) {
            player.setScore(gameScore,true);
        }
        return player;
    }

}