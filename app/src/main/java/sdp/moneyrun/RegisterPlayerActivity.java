package sdp.moneyrun;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class RegisterPlayerActivity extends AppCompatActivity {
    private Button submitButton;
    private EditText nameText;
    private EditText addressText;
    private EditText colorText;
    private EditText animalText;
    private String[] result;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_profile);
        submitButton = findViewById(R.id.submitProfileButton);
        nameText = findViewById(R.id.registerNameText);
        addressText = findViewById(R.id.registerAddressText);
        colorText = findViewById(R.id.registerColorText);
        animalText = findViewById(R.id.registerAnimalText);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAllFields(nameText.getText().toString(),addressText.getText().toString(), colorText.getText().toString(),animalText.getText().toString())){
                    Random random = new Random();
                    int uniquePlayerID = random.nextInt();
                    while(uniquePlayerID < 0)
                        uniquePlayerID = random.nextInt();
//                    Player player = new Player(uniquePlayerID);
                    //TODO:place it into the database with uniquePlayerID as key
                    //TODO : check if there is a player with that unique ID already in database and if there is change ID
                    Intent menuIntent = new Intent(RegisterPlayerActivity.this, MenuActivity.class);
                    menuIntent.putExtra("playerId",uniquePlayerID);
                    menuIntent.putExtra("playerId"+uniquePlayerID,result);
                    startActivity(menuIntent);
                }
            }
        });

    }
    private boolean checkAllFields(String name, String address, String color, String animal){
        if(name.trim().isEmpty() || address.trim().isEmpty() || color.trim().isEmpty() || animal.trim().isEmpty()){
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
            return false;
        }
            result = new String[4];
            result[0] = name;
            result[1] = address;
            result[2] = "0";
            result[3] = "0";
            return true;
    }
}
