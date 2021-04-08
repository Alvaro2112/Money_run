package sdp.moneyrun;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PlayerProfileActivity extends AppCompatActivity {
    public TextView playerName;
    public TextView playerAddress;
    public TextView playerDiedGames;
    public TextView playerPlayedGames;
    public TextView playerIsEmptyText;
    public Button goBackToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        playerName = findViewById(R.id.playerName);
        playerAddress = findViewById(R.id.playerAddress);
        playerDiedGames = findViewById(R.id.playerDiedGames);
        playerPlayedGames = findViewById(R.id.playerPlayedGames);
        playerIsEmptyText = findViewById(R.id.playerEmptyMessage);
        goBackToMain = findViewById(R.id.goBackToMainMenu);
        goBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainMenuIntent = new Intent(PlayerProfileActivity.this, MenuActivity.class);
                startActivity(mainMenuIntent);
            }
        });
        Intent playerIntent = getIntent();
        int playerId = playerIntent.getIntExtra("playerId",0);
        String[] playerInfo = playerIntent.getStringArrayExtra("playerId"+playerId);
        setDisplayedTexts(playerInfo);

//        Player dummy1 = new Player(1000000,"George","New Delhi",0,0);
//        DatabaseProxy databaseProxy = new DatabaseProxy();
//        databaseProxy.putPlayer(dummy1);
    }

    public void setDisplayedTexts(String[] playerInfo) {
        if (playerInfo == null || playerInfo.length == 0) {
            playerIsEmptyText.setAllCaps(true);
            playerIsEmptyText.setText("PLAYER IS EMPTY GO BACK TO MAIN MANY TO FILL UP THE INFO FOR THE PLAYER");
        } else {
            playerName.setText("Player name : " + playerInfo[0]);
            playerAddress.setText("Player address : " + playerInfo[1]);
            playerDiedGames.setText("Player has died " + playerInfo[2] + " many times");
            playerPlayedGames.setText("Player has played " + playerInfo[3] + " many games");
        }
    }
}
