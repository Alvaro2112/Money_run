package sdp.moneyrun;

import android.location.Location;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity (tableName = "riddles")
class Riddle {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "Questions")
    private final String question;

    @ColumnInfo(name = "Correct")
    private final String firstAnswer;

    @ColumnInfo(name = "A")
    private final String secondAnswer;

    @ColumnInfo(name = "B")
    private final String thirdAnswer;

    @ColumnInfo(name = "C")
    private final String fourthAnswer;

    @ColumnInfo(name = "D")
    private final String correctAnswer;


    /**
     * @param question        This is the question that the user will see and will have to solve
     * @param correctAnswer   This is the unique correct answer to the question
     */
    public Riddle(String question, String firstAnswer, String secondAnswer, String thirdAnswer, String fourthAnswer, String correctAnswer) {
        if (question == null || firstAnswer == null || correctAnswer == null || secondAnswer == null)
            throw new IllegalArgumentException("Null arguments in Riddle constructor");

        String[] possibleAnswers = {firstAnswer, secondAnswer, thirdAnswer, fourthAnswer};
        Set<String> possibleAnswersAsSet = new HashSet<>(Arrays.asList(possibleAnswers));

        if (!possibleAnswersAsSet.contains(correctAnswer)) {
            throw new IllegalArgumentException("The correct solution must be one of the possible answers");
        }

        this.question = question;
        this.correctAnswer = correctAnswer;
        this.firstAnswer = firstAnswer;
        this.secondAnswer= secondAnswer;
        this.thirdAnswer = thirdAnswer;
        this.fourthAnswer = fourthAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return correctAnswer;
    }

    public String[] getPossibleAnswers() {
        return new String[]{firstAnswer, secondAnswer, thirdAnswer, fourthAnswer};
    }

    public String getFirstAnswer() {
        return firstAnswer;
    }

    public String getSecondAnswer() {
        return secondAnswer;
    }

    public String getThirdAnswer() {
        return thirdAnswer;
    }

    public String getFourthAnswer() {
        return fourthAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

}


// The entirety of the game logic should be implemented in this class
public class Game {
    private List<Player> players;
    private List<Riddle> riddles;
    private Location startLocation;//TODO: check if we will use the existing or create a new class Location

    public Game(List<Player> players, List<Riddle> riddles, Location startLocation) {
        if (players == null || riddles == null || startLocation == null || players.isEmpty() || riddles.isEmpty())
            throw new IllegalArgumentException("Null parameter passed as argument in Game constructor");

        this.players = players;
        this.riddles = riddles;
        this.startLocation = startLocation;
    }

    public static void startGame(Game game) {
        game.startGame();
    }

    // Launched when create game button is pressed
    public void startGame() {

    }

    public boolean askPlayer(Player player, Riddle riddle) {
        String playerResponse = player.ask(riddle.getQuestion());
        return playerResponse.trim().replaceAll(" ", "").toLowerCase().equals(riddle.getAnswer());
    }

    /**
     *
     * @return returns a random riddle from all the possible riddles
     */
    public Riddle getRandomRiddle(){

        int index = (int)(Math.random() * (riddles.size()));
        return riddles.get(index);
    }


}
