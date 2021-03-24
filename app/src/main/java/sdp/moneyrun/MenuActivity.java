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
    private String[] result;
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Button profileButton = findViewById(R.id.go_to_profile_button);
        Button joinGame = findViewById(R.id.join_game);
        Button askQuestion = findViewById(R.id.ask_question);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonSwitchToUserProfileActivity(v);
            }
        });

        /**
         * Checks for clicks on the join game button and creates a popup of available games if clicked
         */
        joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonShowJoinGamePopupWindowClick(v, true, R.layout.join_game_popup);
            }
        });

        /**
         * Checks for clicks on the ask question button and creates a popup of a new question of clicked
         */
        askQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonShowQuestionPopupWindowClick(v, true, R.layout.question_popup);
            }
        });
    }

    public void onButtonSwitchToUserProfileActivity(View view) {

        Intent playerProfileIntent = new Intent(MenuActivity.this, PlayerProfileActivity.class);
        playerProfileIntent.putExtra("profile", result);
        startActivity(playerProfileIntent);

    }

    public void onButtonShowJoinGamePopupWindowClick(View view, Boolean focusable, int layoutId) {

        onButtonShowPopupWindowClick(view, focusable, layoutId);

    }


    public void onButtonShowQuestionPopupWindowClick(View view, Boolean focusable, int layoutId) {


        String question = "One of these four countries does not border the Red Sea.";
        String correctAnswer = "Oman";
        String[] possibleAnswers = {"Jordan", "Oman", "Sudan"};
        Riddle riddle = new Riddle(question, possibleAnswers, correctAnswer);

        PopupWindow popupWindow = onButtonShowPopupWindowClick(view, focusable, layoutId);
        TextView tv = popupWindow.getContentView().findViewById(R.id.question);
        int correctId = 0;
        tv.setText(riddle.getQuestion());

        int[] buttonIds = {R.id.question_choice_1, R.id.question_choice_2, R.id.question_choice_3, R.id.question_choice_4};
        for (int i = 0; i < 4; i++){
            if(i >= possibleAnswers.length){
                popupWindow.getContentView().findViewById(buttonIds[i]).setVisibility(View.GONE);
                continue;
            }
            tv = popupWindow.getContentView().findViewById(buttonIds[i]);
            tv.setText(possibleAnswers[i]);
            if(possibleAnswers[i].equals(correctAnswer))
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