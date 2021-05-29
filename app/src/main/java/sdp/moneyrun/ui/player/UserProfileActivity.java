package sdp.moneyrun.ui.player;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.Objects;

import sdp.moneyrun.R;
import sdp.moneyrun.database.DatabaseProxy;
import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.user.User;

public class UserProfileActivity extends AppCompatActivity {
    private final String TAG = UserProfileActivity.class.getSimpleName();
    public TextView playerName;
    public TextView playerDiedGames;
    public TextView playerPlayedGames;
    public Button goBackToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_user_profile);
        playerName = findViewById(R.id.playerName);
        playerDiedGames = findViewById(R.id.playerDiedGames);
        playerPlayedGames = findViewById(R.id.playerPlayedGames);
        goBackToMain = findViewById(R.id.goBackToMainMenu);

        Intent playerIntent = getIntent();
        User user = (User) playerIntent.getSerializableExtra("user");

        goBackToMain.setOnClickListener(v -> {
            MediaPlayer.create(this, R.raw.button_press).start();
            Intent mainMenuIntent = new Intent(UserProfileActivity.this, MenuActivity.class);
            mainMenuIntent.putExtra("user", user);
            startActivity(mainMenuIntent);
            finish();
        });

        setDisplayedTexts(user);
        DatabaseProxy.addOfflineListener(this, TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseProxy.removeOfflineListener();
    }

    public void setDisplayedTexts(@Nullable User user) {
        if (user == null) {
            playerName.setText("");
            playerDiedGames.setText("");
            playerPlayedGames.setText(R.string.profile_never_created);
        } else {
            playerName.setText(user.getName());

            String gamesLost = String.format(Locale.getDefault(), "Number of games lost\n\n%d", user.getNumberOfDiedGames());
            SpannableString gamesLostContent = new SpannableString(gamesLost);
            gamesLostContent.setSpan(new UnderlineSpan(), 0, gamesLost.length()- Integer.toString(user.getNumberOfDiedGames()).length(), 0);

            String gamesPlayed = String.format(Locale.getDefault(), "Number of games played\n\n%d", user.getNumberOfPlayedGames());
            SpannableString gamesPlayedContent = new SpannableString(gamesPlayed);
            gamesPlayedContent.setSpan(new UnderlineSpan(), 0, gamesLost.length()- Integer.toString(user.getNumberOfPlayedGames()).length(), 0);

            playerDiedGames.setText(gamesLostContent);
            playerPlayedGames.setText(gamesPlayedContent);
        }
    }

    @Override
    public void onBackPressed() {
    }
}
