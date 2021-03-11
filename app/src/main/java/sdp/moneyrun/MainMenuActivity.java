package sdp.moneyrun;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainMenuActivity extends AppCompatActivity {
    public EditText viewProfile;
    public Button query_button;
    public TextView resultProfile;
    public Button showProfile;
    public Button createProfile;
    private String[] result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        viewProfile = findViewById(R.id.viewProfile);
        query_button = findViewById(R.id.query_button);
        resultProfile = findViewById(R.id.resultProfile);
        showProfile = findViewById(R.id.show_profile);
        createProfile = findViewById(R.id.createProfile);

        query_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseTrial databaseTrial = DatabaseTrial.getDatabaseTrialInstance(getApplicationContext());
                databaseTrial.openDatabase();
                String q = viewProfile.getText().toString();
                result = databaseTrial.query(Integer.parseInt(q));
                resultProfile.setText(databaseTrial.displayQueryAsString(result));
                databaseTrial.closeDatabase();
            }
        });
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