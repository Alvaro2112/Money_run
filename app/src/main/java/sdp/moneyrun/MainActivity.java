package sdp.moneyrun;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public EditText viewProfile;
    public Button query_button;
    public TextView resultProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewProfile = findViewById(R.id.viewProfile);
        query_button = findViewById(R.id.query_button);
        resultProfile = findViewById(R.id.resultProfile);

        query_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseTrial databaseTrial = DatabaseTrial.getDatabaseTrialInstance(getApplicationContext());
                databaseTrial.openDatabase();
                String q = viewProfile.getText().toString();
                String result = databaseTrial.query(Integer.parseInt(q));
                resultProfile.setText(result);
                databaseTrial.closeDatabase();
            }
        });
    }
}