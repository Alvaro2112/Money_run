package sdp.moneyrun;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Semaphore;

import sdp.moneyrun.map.MapActivity;


import sdp.moneyrun.menu.JoinGameImplementation;
import sdp.moneyrun.menu.MenuImplementation;
import sdp.moneyrun.menu.NewGameImplementation;


public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final ActivityResultLauncher<String[]> requestPermissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), map -> {});

    private String[] playerInfo;
    private int playerId;
    private RiddlesDatabase db;
    private Button mapButton;
    protected DrawerLayout mDrawerLayout;
    private final Semaphore available = new Semaphore(1, true);
    private int numberOfAsyncTasks;
    private int tasksFInished;

    DatabaseReference databaseReference;
    FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);
        setNavigationViewListener();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mapButton = findViewById(R.id.map_button);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        // setup database instance
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        // Get player location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            db = RiddlesDatabase.createInstance(getApplicationContext());
        } catch (RuntimeException e) {
            db = RiddlesDatabase.getInstance();
        }

        runFunctionalities();
    }

    public void runFunctionalities(){
        JoinGameImplementation joinGameImplementation = new JoinGameImplementation(this,
                databaseReference,
                requestPermissionsLauncher,
                fusedLocationClient,
                true,
                R.layout.join_game_popup);

        NewGameImplementation newGameImplementation = new NewGameImplementation(this,
                databaseReference,
                requestPermissionsLauncher,
                fusedLocationClient);

        // Functionalities
        mapButton = findViewById(R.id.map_button);
        addMapButtonFunctionality();

        Button joinGame = findViewById(R.id.join_game);
        joinGame.setOnClickListener(joinGameImplementation::onClickShowJoinGamePopupWindow);

        Button newGame = findViewById(R.id.new_game);
        newGame.setOnClickListener(newGameImplementation::onClickShowNewGamePopupWindow);

        addAskQuestionButtonFunctionality();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RiddlesDatabase.reset();
    }

    public void StartMapActivity(){
        Intent mainIntent = new Intent(MenuActivity.this, MapActivity.class);
        MenuActivity.this.startActivity(mainIntent);
        MenuActivity.this.finish();
        available.release();
    }

    public void addMapButtonFunctionality(){

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Example of how the Async tasks should be implemented
                numberOfAsyncTasks = 2;
                tasksFInished = 0;
                setContentView(R.layout.splash_screen);

                Runnable x = new Runnable() {
                    public void run() {
                        synchronized (this) {
                            try {
                                wait(5000);
                            } catch (InterruptedException e) {
                            }
                        }
                        try {
                            available.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(tasksFInished == numberOfAsyncTasks - 1){
                            StartMapActivity();
                        } else {
                            tasksFInished += 1;
                        }

                        available.release();
                    }
                };

                Runnable y = new Runnable() {
                    public void run() {
                        synchronized (this) {
                            try {
                                wait(2000);
                            } catch (InterruptedException e) {
                            }
                        }
                        try {
                            available.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(tasksFInished == numberOfAsyncTasks - 1){
                            StartMapActivity();
                        } else {
                            tasksFInished += 1;
                        }

                        available.release();
                    }
                };

                Thread thread = new Thread(x);
                Thread thread1 = new Thread(y);

                thread.start();
                thread1.start();
            }

        });
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.profile_button: {
                onButtonSwitchToUserProfileActivity(item.getActionView());
                break;
            }

            case R.id.leaderboard_button: {
                Intent leaderboardIntent = new Intent(MenuActivity.this, LeaderboardActivity.class);
                startActivity(leaderboardIntent);
                break;
            }

            case R.id.log_out_button: {
                FirebaseAuth.getInstance().signOut();
                finish();
                break;
            }
        }
        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void addAskQuestionButtonFunctionality(){
        Button askQuestion = findViewById(R.id.ask_question);
        askQuestion.setOnClickListener(v -> onButtonShowQuestionPopupWindowClick(v, true, R.layout.question_popup, db.getRandomRiddle()));
    }

    private void setPutExtraArguments(Intent intent){
        intent.putExtra("playerId",playerId);
        intent.putExtra("playerId"+playerId,playerInfo);
    }

    public void onButtonSwitchToUserProfileActivity(View view) {

        Intent playerProfileIntent = new Intent(MenuActivity.this, PlayerProfileActivity.class);
        setPutExtraArguments(playerProfileIntent);
        startActivity(playerProfileIntent);
    }

    public void onButtonShowJoinGamePopupWindowClick(View view, Boolean focusable, int layoutId) {

        onButtonShowPopupWindowClick(view, focusable, layoutId);
    }

    /**
     *
     * @param view Current view before click
     * @param focusable Whether it can be dismissed by clicking outside the popup window
     * @param layoutId Id of the popup layout that will be used
     */
    public PopupWindow onButtonShowPopupWindowClick(View view, Boolean focusable, int layoutId) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(layoutId, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window at wanted location
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        return popupWindow;
    }
    //TODO: fix it somehow: task is never completed and thus cannot get player from database
    //To come back too later
//    public void setPlayerObject(){
//        playerId = getIntent().getIntExtra("playerId",0);
//        playerInfo = getIntent().getStringArrayExtra("playerId"+playerId);
//        DatabaseProxy db = new DatabaseProxy();
//        if(db != null) {
//            Task<DataSnapshot> t = db.getPlayerTask(playerId);
////            player = db.getPlayerFromTask(t);
//            t.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DataSnapshot> task) {
//                    if(task.isSuccessful()){
//                        player = db.getPlayerFromTask(t);
//                    }
//                }
//            });
////           while(!t.isComplete()){
////               System.out.println("Task is not ready yet");
////           }
//            System.out.println("PLayer should be set by now");
//        }
//        //TODO: put player in the database with playerId as primary key
//    }

    public void onButtonShowQuestionPopupWindowClick(View view, Boolean focusable, int layoutId, Riddle riddle) {

        PopupWindow popupWindow = onButtonShowPopupWindowClick(view, focusable, layoutId);
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
