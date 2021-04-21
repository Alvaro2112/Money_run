package sdp.moneyrun.ui.game;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import sdp.moneyrun.R;
import sdp.moneyrun.ui.menu.MenuActivity;

public class GameLobbyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_lobby);

        findViewById(R.id.leave_lobby_button).setOnClickListener(v ->  {
            startActivity(new Intent(getApplicationContext(), MenuActivity.class));
            finish();
        });

        String gameId = (String) getIntent().getSerializableExtra("currentGameId");
    }
}