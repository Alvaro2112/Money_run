package sdp.moneyrun.ui.player;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.R;
import sdp.moneyrun.player.Player;

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

        Intent playerIntent = getIntent();
        Player user = (Player) playerIntent.getSerializableExtra("user");

        goBackToMain.setOnClickListener(v -> {
            Intent mainMenuIntent = new Intent(PlayerProfileActivity.this, MenuActivity.class);
            mainMenuIntent.putExtra("user", user);
            startActivity(mainMenuIntent);
            finish();
        });

        setDisplayedTexts(user);
    }

    public void setDisplayedTexts(Player user) {
        if (user == null) {
            playerIsEmptyText.setAllCaps(true);
            playerIsEmptyText.setText("PLAYER IS EMPTY GO BACK TO MAIN MANY TO FILL UP THE INFO FOR THE PLAYER");
        } else {
            playerName.setText("Player name : " + user.getName());
            playerAddress.setText("Player address : " + user.getAddress());
            playerDiedGames.setText("Player has died " + user.getNumberOfDiedGames() + " many times");
            playerPlayedGames.setText("Player has played " + user.getNumberOfPlayedGames() + " many games");
        }
    }
}
