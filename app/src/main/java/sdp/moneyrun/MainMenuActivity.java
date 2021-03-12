package sdp.moneyrun;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainMenuActivity extends AppCompatActivity {
    public Button showProfile;
    public Button createProfile;
    private String[] result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        showProfile = findViewById(R.id.show_profile);
        createProfile = findViewById(R.id.createProfile);

        showProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playerProfileIntent = new Intent(MainMenuActivity.this, PlayerProfileActivity.class);
                playerProfileIntent.putExtra("profile", result);
                startActivity(playerProfileIntent);
            }
        });
        createProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createProfileIntent = new Intent(MainMenuActivity.this, CreatePlayerProfileActivity.class);
                startActivity(createProfileIntent);
            }
        });
    }
}