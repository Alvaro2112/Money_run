package sdp.moneyrun.ui.player;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import sdp.moneyrun.R;
import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.user.User;

public class UserProfileActivity extends AppCompatActivity {
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
        User user = (User) playerIntent.getSerializableExtra("user");

        goBackToMain.setOnClickListener(v -> {
            Intent mainMenuIntent = new Intent(UserProfileActivity.this, MenuActivity.class);
            mainMenuIntent.putExtra("user", user);
            startActivity(mainMenuIntent);
            finish();
        });

        setDisplayedTexts(user);
    }

    public void setDisplayedTexts(User user) {
        if (user == null) {
            playerIsEmptyText.setAllCaps(true);
            playerIsEmptyText.setText(R.string.fillup_player_warning);
        } else {
            playerName.setText(String.format("User name : %s", user.getName()));
            playerAddress.setText(String.format("User address : %s", user.getAddress()));
            playerDiedGames.setText(String.format(Locale.getDefault(), "User has died %d many times", user.getNumberOfDiedGames()));
            playerPlayedGames.setText(String.format(Locale.getDefault(), "User has played %d many games", user.getNumberOfPlayedGames()));
        }
    }
}
