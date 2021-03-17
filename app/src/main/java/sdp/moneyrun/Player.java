package sdp.moneyrun;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Player extends AppCompatActivity {
    private int playerId;
    private String name;
    private String address;
    private int numberOfPlayedGames;
    private int numberOfDiedGames;

    public Player(int playerId){
        this.playerId = playerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void updatePlayedGames(){
        numberOfPlayedGames++;
    }
    public void updateDiedGames(){
        numberOfDiedGames++;
    }

    public int getNumberOfDiedGames() {
        return numberOfDiedGames;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getAddress() {
        if(address == null)
            throw new IllegalStateException();
        return address;
    }

    public String getName() {
        if(name == null)
            throw new IllegalStateException();
        return name;
    }

    public int getNumberOfPlayedGames() {
        return numberOfPlayedGames;
    }

    public String ask(String question){
        String answer = "";
        //TODO: display question on  player's screen and store the response
        TextView questionText = findViewById(R.id.playerQuestionText);
        EditText answerText = findViewById(R.id.playerAnswerText);
        Button answerButton = findViewById(R.id.playerAnswerButton);
        questionText.setText(question);
        Intent answerQuestionIntent = new Intent(Player.this, QuestionDisplayActivity.class);
        startActivity(answerQuestionIntent);
        while (!answerButton.isPressed()){

        }
        return answerText.getText().toString();
    }
    //TODO: add later methods related to the game itself
}
