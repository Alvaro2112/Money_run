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
import sdp.moneyrun.database.UserDatabaseProxy;
import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.user.User;

@SuppressWarnings("FieldCanBeLocal")
public class RegisterUserActivity extends AppCompatActivity {
    private Button submitButton;
    private EditText nameText;
    private EditText addressText;
    private EditText colorText;
    private EditText animalText;
    private String[] result;
    private UserDatabaseProxy pdb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_profile);
        submitButton = findViewById(R.id.submitProfileButton);
        nameText = findViewById(R.id.registerNameText);
        addressText = findViewById(R.id.registerAddressText);
        colorText = findViewById(R.id.registerColorText);
        animalText = findViewById(R.id.registerAnimalText);

        submitButton.setOnClickListener(v -> {
            if (checkAllFields(nameText.getText().toString(), addressText.getText().toString(),
                    colorText.getText().toString(), animalText.getText().toString())) {
                setRegisterFieldsForNextActivity();
            }
        });

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
        user.setName(result[0]);
        user.setAddress(result[1]);

        pdb = new UserDatabaseProxy();
        pdb.putUser(user);

        menuIntent.putExtra("user", user);
        startActivity(menuIntent);
        finish();
    }

    /*
     Checking on submit that each field is not left empty and raise an error and prevent from logging in if that is the case
     */
    private boolean checkAllFields(@NonNull String name, @NonNull String address, @NonNull String color, @NonNull String animal) {
        if (name.trim().isEmpty() || address.trim().isEmpty() || color.trim().isEmpty() || animal.trim().isEmpty()) {
            setErrorForEmptyFields(name, address, color, animal);
            return false;
        }
        result = new String[4];
        result[0] = name;
        result[1] = address;
        result[2] = "0";
        result[3] = "0";
        return true;
    }

    private void setErrorForEmptyFields(@NonNull String name, @NonNull String address, @NonNull String color, @NonNull String animal) {
        if (name.trim().isEmpty()) {
            nameText.setError("Name field is empty");
        }
        if (address.trim().isEmpty()) {
            addressText.setError("Address field is empty");
        }
        if (color.trim().isEmpty()) {
            colorText.setError("Color field is empty");
        }
        if (animal.trim().isEmpty()) {
            animalText.setError("Animal field is empty");
        }
    }
}
