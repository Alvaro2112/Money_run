package sdp.moneyrun;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;

public class MenuActivity extends AppCompatActivity /*implements NavigationView.OnNavigationItemSelectedListener*/ {

    private Button profileButton;
    private Button leaderboardButton;
    private Button joinGame;
    private String[] result;
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
      
        NavigationView navigationView = findViewById(R.id.nav_view);
        profileButton = findViewById(R.id.go_to_profile_button);
        leaderboardButton = findViewById(R.id.menu_leaderboardButton);
      
        addJoinGameButtonFunctionality();
        addAskQuestionButtonFunctionality();
        linkProfileButton(profileButton);
        linkLeaderboardButton(leaderboardButton);
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
                //Temporary, will be removed when questions are added to the database
                String question = "One of these four countries does not border the Red Sea.";
                String correctAnswer = "Oman";
                String[] possibleAnswers = {"Jordan", "Oman", "Sudan"};
                Riddle riddle = new Riddle(question, possibleAnswers, correctAnswer);
                onButtonShowQuestionPopupWindowClick(v, true, R.layout.question_popup, riddle);
            }
        });

    }

    private void linkProfileButton(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonSwitchToUserProfileActivity(v);
            }
        });
    }

    
    private void linkLeaderboardButton(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent leaderboardIntent = new Intent(MenuActivity.this, LeaderboardActivity.class);
                startActivity(leaderboardIntent);
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
            if(i >= riddle.getPossibleAnswers().length){
                popupWindow.getContentView().findViewById(buttonIds[i]).setVisibility(View.GONE);
                continue;
            }
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