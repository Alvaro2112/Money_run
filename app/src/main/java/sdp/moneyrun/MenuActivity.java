package sdp.moneyrun;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.menu.JoinGameImplementation;
import sdp.moneyrun.menu.MenuImplementation;
import sdp.moneyrun.menu.NewGameImplementation;


public class MenuActivity extends AppCompatActivity /*implements NavigationView.OnNavigationItemSelectedListener*/ {
    private final ActivityResultLauncher<String[]> requestPermissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), map -> {});

    private Button profileButton;
    private Button leaderboardButton;

    private String[] result;
    private Player player;
    private RiddlesDatabase db;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    // Get player location
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // setup database instance
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        // Get player location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            db = RiddlesDatabase.createInstance(getApplicationContext());
        } catch (RuntimeException e) {
            db = RiddlesDatabase.getInstance();
        }

        // Every buttons, elements on the activity
        profileButton = findViewById(R.id.go_to_profile_button);
        leaderboardButton = findViewById(R.id.menu_leaderboardButton);


        Button joinGame = findViewById(R.id.join_game);
        joinGame.setOnClickListener(v -> JoinGameImplementation.onClickShowJoinGamePopupWindow(v,
                this,
                databaseReference,
                true,
                R.layout.join_game_popup,
                requestPermissionsLauncher,
                fusedLocationClient));

        Button newGame = findViewById(R.id.new_game);
        newGame.setOnClickListener(v -> NewGameImplementation.onClickShowNewGamePopupWindow(v,
                this,
                databaseReference,
                requestPermissionsLauncher,
                fusedLocationClient));

        addAskQuestionButtonFunctionality();
        addLogOutButtonFunctionality();

        profileButton.setOnClickListener(this::onButtonSwitchToUserProfileActivity);

        leaderboardButton.setOnClickListener(v -> {
            Intent leaderboardIntent = new Intent(MenuActivity.this, LeaderboardActivity.class);
            startActivity(leaderboardIntent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RiddlesDatabase.reset();
    }

    public void addLogOutButtonFunctionality() {

        Button logOut = findViewById(R.id.log_out_button);

        /**
         * Checks for clicks on the join game button and creates a popup of available games if clicked
         */
        logOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });
    }

    public void addAskQuestionButtonFunctionality() {

        Button askQuestion = findViewById(R.id.ask_question);
        askQuestion.setOnClickListener(v -> onButtonShowQuestionPopupWindowClick(v, true, R.layout.question_popup, db.getRandomRiddle()));
    }

    public void onButtonSwitchToUserProfileActivity(View view) {

        Intent playerProfileIntent = new Intent(MenuActivity.this, PlayerProfileActivity.class);
        int playerId = getIntent().getIntExtra("playerId", 0);
        String[] playerInfo = getIntent().getStringArrayExtra("playerId" + playerId);
        playerProfileIntent.putExtra("playerId", playerId);
        playerProfileIntent.putExtra("playerId" + playerId, playerInfo);
        startActivity(playerProfileIntent);
    }

    public void onButtonShowQuestionPopupWindowClick(View view, Boolean focusable, int layoutId, Riddle riddle) {

        PopupWindow popupWindow = MenuImplementation.onButtonShowPopupWindowClick(view , this, focusable, layoutId);
        TextView tv = popupWindow.getContentView().findViewById(R.id.question);
        int correctId = 0;

        //changes the text to the current question
        tv.setText(riddle.getQuestion());

        int[] buttonIds = {R.id.question_choice_1, R.id.question_choice_2, R.id.question_choice_3, R.id.question_choice_4};
        TextView buttonView = tv;

        //Loops to find the ID of the button solution and assigns the text to each button
        for (int i = 0; i < 4; i++) {

            buttonView = popupWindow.getContentView().findViewById(buttonIds[i]);
            buttonView.setText(riddle.getPossibleAnswers()[i]);

            if (riddle.getPossibleAnswers()[i].equals(riddle.getAnswer()))
                correctId = buttonIds[i];
        }

        popupWindow.getContentView().findViewById(correctId).setOnClickListener(v -> popupWindow.dismiss());
    }
}