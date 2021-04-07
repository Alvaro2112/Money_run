package sdp.moneyrun;

import android.content.Intent;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.InputStream;

import sdp.moneyrun.map.MapActivity;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Button profileButton;
    private Button leaderboardButton;
    private Button joinGame;
    private String[] result;
    private Player player;
    private RiddlesDatabase db;
    private Button mapButton;
    protected DrawerLayout mDrawerLayout;
    private Button logOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setNavigationViewListener();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mapButton = findViewById(R.id.map_button);
        try{
            db = RiddlesDatabase.createInstance(getApplicationContext());
        }
        catch(RuntimeException e){
            db = RiddlesDatabase.getInstance();
        }

        logOut = findViewById(R.id.log_out_button);

        addJoinGameButtonFunctionality();
        addMapButtonFunctionality();
        addAskQuestionButtonFunctionality();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RiddlesDatabase.reset();
    }

    public void addMapButtonFunctionality(){

        mapButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent leaderboardIntent = new Intent(MenuActivity.this, MapActivity.class);
                startActivity(leaderboardIntent);
            }
        });
    }

  /*          new Handler().postDelayed(new Runnable(){
        @Override
        public void run() {
            /* Create an Intent that will start the Menu-Activity. *//*
            Intent mainIntent = new Intent(Splash.this,Menu.class);
            MapActivity.this.startActivity(mainIntent);
            MapActivity.this.finish();
        }
    }, SPLASH_DISPLAY_LENGTH);*/




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

    public void addJoinGameButtonFunctionality(){

        Button joinGame = findViewById(R.id.join_game);

        /**
         * Checks for clicks on the join game button and creates a popup of available games if clicked
         */
        joinGame.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onButtonShowJoinGamePopupWindowClick(v, true, R.layout.join_game_popup);
            }
        });
    }


    public void addAskQuestionButtonFunctionality(){

        Button askQuestion = findViewById(R.id.ask_question);

        /**
         * Checks for clicks on the ask question button and creates a popup of a new question of clicked
         */
        askQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onButtonShowQuestionPopupWindowClick(v, true, R.layout.question_popup, db.getRandomRiddle());
            }
        });

    }


    public void onButtonSwitchToUserProfileActivity(View view) {

        Intent playerProfileIntent = new Intent(MenuActivity.this, PlayerProfileActivity.class);
        int playerId = getIntent().getIntExtra("playerId",0);
        String[] playerInfo = getIntent().getStringArrayExtra("playerId"+playerId);
        playerProfileIntent.putExtra("playerId",playerId);
        playerProfileIntent.putExtra("playerId"+playerId,playerInfo);
        startActivity(playerProfileIntent);

    }

    public void onButtonShowJoinGamePopupWindowClick(View view, Boolean focusable, int layoutId) {

        onButtonShowPopupWindowClick(view, focusable, layoutId);

    }


    public void onButtonShowQuestionPopupWindowClick(View view, Boolean focusable, int layoutId, Riddle riddle) {

        PopupWindow popupWindow = onButtonShowPopupWindowClick(view, focusable, layoutId);
        TextView tv = popupWindow.getContentView().findViewById(R.id.question);
        int correctId = 0;

        //changes the text to the current question
        tv.setText(riddle.getQuestion());

        int[] buttonIds = {R.id.question_choice_1, R.id.question_choice_2, R.id.question_choice_3, R.id.question_choice_4};
        TextView buttonView = tv;

        //Loops to find the ID of the button solution and assigns the text to each button
        for (int i = 0; i < 4; i++){

            buttonView = popupWindow.getContentView().findViewById(buttonIds[i]);
            buttonView.setText(riddle.getPossibleAnswers()[i]);

            if(riddle.getPossibleAnswers()[i].equals(riddle.getAnswer()))
                correctId = buttonIds[i];
        }

        popupWindow.getContentView().findViewById(correctId).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

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



//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.profile_button:
////                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlayerProfileFragment()).commit();
//                break;
//        }
//        return false;
//    }
}