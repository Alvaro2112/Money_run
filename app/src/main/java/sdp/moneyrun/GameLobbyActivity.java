package sdp.moneyrun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class GameLobbyActivity extends AppCompatActivity {
    private Button leave;
    private Button launch;
    private TextView playersMissing;
    private TextView playerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_lobby);

        leave = (Button) findViewById(R.id.leave_lobby_button);
        launch = (Button) findViewById(R.id.launch_game_button);
        playersMissing = (TextView) findViewById(R.id.players_missing_TextView);
        playerList = (TextView) findViewById(R.id.player_list_textView);

        Intent intent = getIntent();

    }




}