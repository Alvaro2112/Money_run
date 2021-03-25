package sdp.moneyrun;

import android.location.Location;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Riddle {
    private final String question;
    private final String[] possibleAnswers;
    private final String correctAnswer;


    /**
     * @param question        This is the question that the user will see and will have to solve
     * @param possibleAnswers This is all the possible answers the users will be offered
     * @param correctAnswer   This is the unique correct answer to the question
     */
    public Riddle(String question, String[] possibleAnswers, String correctAnswer) {
        if (question == null || possibleAnswers == null || correctAnswer == null)
            throw new IllegalArgumentException("Null arguments in Riddle constructor");

        Set<String> possibleAnswersAsSet = new HashSet<>(Arrays.asList(possibleAnswers));

        if (!possibleAnswersAsSet.contains(correctAnswer)) {
            throw new IllegalArgumentException("The correct solution must be one of the possible answers");
        }

        this.question = question;
        this.possibleAnswers = possibleAnswers;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return correctAnswer;
    }

    public String[] getPossibleAnswers() {
        return possibleAnswers;
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
