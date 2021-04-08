package sdp.moneyrun;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

import javax.security.auth.callback.Callback;

import sdp.moneyrun.map.MapActivity;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Button profileButton;
    private Button leaderboardButton;
    private Button joinGame;
    private String[] playerInfo;
    private Player player;
    private int playerId;
    private RiddlesDatabase db;
    private Button mapButton;
    protected DrawerLayout mDrawerLayout;
    private Button logOut;
    private final Semaphore available = new Semaphore(1, true);
    private int numberOfAsyncTasks;
    private int tasksFInished;




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
    
}