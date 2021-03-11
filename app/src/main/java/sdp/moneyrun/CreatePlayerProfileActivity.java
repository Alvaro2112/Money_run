package sdp.moneyrun;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CreatePlayerProfileActivity extends AppCompatActivity {
    public Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        submit = findViewById(R.id.submitProfile);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainMenuIntent = new Intent(CreatePlayerProfileActivity.this, MainActivity.class);
                startActivity(mainMenuIntent);
            }
        });
    }
}
