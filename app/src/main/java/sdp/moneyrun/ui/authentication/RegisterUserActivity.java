package sdp.moneyrun.ui.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import sdp.moneyrun.R;
import sdp.moneyrun.database.DatabaseProxy;
import sdp.moneyrun.database.UserDatabaseProxy;
import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.user.User;

@SuppressWarnings("FieldCanBeLocal")
public class RegisterUserActivity extends AppCompatActivity {
    private Button submitButton;
    private EditText nameText;
    private UserDatabaseProxy pdb;
    private String nameResult;
    private final String TAG = RegisterUserActivity.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_profile);
        submitButton = findViewById(R.id.submitProfileButton);
        nameText = findViewById(R.id.registerNameText);

        submitButton.setOnClickListener(v -> {
            if (checkAllFields(nameText.getText().toString())) {
                setRegisterFieldsForNextActivity();
            }
        });

        DatabaseProxy.addOfflineListener(this, TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DatabaseProxy.removeOfflineListener();
    }
    @Override
    protected void onResume() {
        super.onResume();
        DatabaseProxy.addOfflineListener(this, TAG);
    }

    protected void onStop(){
        super.onStop();
        DatabaseProxy.removeOfflineListener();
    }

    private void setRegisterFieldsForNextActivity() {
        boolean guestMode = getIntent().getBooleanExtra("guestUser", false);
        String userId = getIntent().getStringExtra("userId");
        Intent menuIntent = new Intent(RegisterUserActivity.this, MenuActivity.class);
        if (guestMode) {
            Random random = new Random();
            userId = Integer.toString(Math.abs(random.nextInt()));
            menuIntent.putExtra("guestUser", true);
        }
        User user = new User(userId);
        user.setName(nameResult);

        pdb = new UserDatabaseProxy();
        pdb.putUser(user);

        menuIntent.putExtra("user", user);
        startActivity(menuIntent);
        finish();
    }

    /*
     Checking on submit that each field is not left empty and raise an error and prevent from logging in if that is the case
     */
    private boolean checkAllFields(@NonNull String name) {
        if (name.trim().isEmpty()) {
            nameText.setError("Name field is empty");
            return false;
        }
        nameResult = name;
        return true;
    }

    @Override
    public void onBackPressed() {
    }
}
