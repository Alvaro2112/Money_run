package sdp.moneyrun;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

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

        TextView gameName = findViewById(R.id.game_name);
        gameName.setText("huhuhuhu" + "[" +gameId + "]");
    }
}