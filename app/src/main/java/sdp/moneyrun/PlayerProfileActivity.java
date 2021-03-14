package sdp.moneyrun;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
        String[] playerInfo = playerIntent.getStringArrayExtra("profile");
        if(playerInfo == null || playerInfo.length == 0){
            playerIsEmptyText.setAllCaps(true);
            playerIsEmptyText.setText("PLAYER IS EMPTY GO BACK TO MAIN MANY TO FILL UP THE INFO FOR THE PLAYER");
        }else {
            playerName.setText("Player name : " + playerInfo[0]);
            playerAddress.setText("Player address : " + playerInfo[1]);
            playerDiedGames.setText("Player has died " + playerInfo[2] + " many times");
            playerPlayedGames.setText("Player has played " + playerInfo[3] + " many games");
        }
    }
}
