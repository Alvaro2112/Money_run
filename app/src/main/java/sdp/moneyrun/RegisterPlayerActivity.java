package sdp.moneyrun;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class RegisterPlayerActivity extends AppCompatActivity {
    private Button submitButton;
    private EditText nameText;
    private EditText addressText;
    private EditText colorText;
    private EditText animalText;
    private String[] result;
    private DatabaseProxy db;
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
            if(checkAllFields(nameText.getText().toString(), addressText.getText().toString(),
                    colorText.getText().toString(), animalText.getText().toString())){
                setRegisterFieldsForNextActivity();
            }
        });

    }
    private void setRegisterFieldsForNextActivity(){
        int playerId = getIntent().getIntExtra("playerId",0);
        Player user = new Player(playerId);
        user.setName(result[0]);
        user.setAddress(result[1]);
        user.setScore(0);

        db = new DatabaseProxy();
        db.putPlayer(user);

        Intent menuIntent = new Intent(RegisterPlayerActivity.this, MenuActivity.class);
        menuIntent.putExtra("user", user);
        startActivity(menuIntent);
        finish();
    }

    /*
     Checking on submit that each field is not left empty and raise an error and prevent from logging in if that is the case
     */
    private boolean checkAllFields(String name, String address, String color, String animal){
        if(name.trim().isEmpty() || address.trim().isEmpty() || color.trim().isEmpty() || animal.trim().isEmpty()){
            setErrorForEmptyFields(name,address,color,animal);
            return false;
        }
        result = new String[4];
        result[0] = name;
        result[1] = address;
        result[2] = "0";
        result[3] = "0";
        return true;
    }
    private void setErrorForEmptyFields(String name, String address, String color, String animal){
        if(name.trim().isEmpty()){
            nameText.setError("Name field is empty");
        }
        if(address.trim().isEmpty()){
            addressText.setError("Address field is empty");
        }
        if(color.trim().isEmpty()){
            colorText.setError("Color field is empty");
        }
        if(animal.trim().isEmpty()){
            animalText.setError("Animal field is empty");
        }
    }
}
