package sdp.moneyrun;

import android.location.Location;

import java.util.List;

class Riddle{
    private String question;
    private String answer;

    public Riddle(String question, String answer){
        if(question == null || answer == null)
            throw new IllegalArgumentException("Null arguments in Riddle constructor");
        this.question = question;
        this.answer = answer;
    }
    public String getQuestion(){
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}


// The entirety of the game logic should be implemented in this class
public class Game {
    private final String name;
    private List<Player> players;
    private int maxPlayerNumber;
    private List<Riddle> riddles;
    private Location startLocation;//TODO: check if we will use the existing or create a new class Location

    public Game(String name, List<Player> players, int maxPlayerNumber, List<Riddle> riddles, Location startLocation){
        if(name == null || players == null || riddles == null || startLocation == null)
            throw new IllegalArgumentException("Null parameter passed as argument in Game constructor");
        this.name = name;
        this.players = players;
        this.riddles = riddles;
        this.startLocation = startLocation;
    }

    public String getName(){
        return name;
    }

    public int getPlayerNumber(){
        return players.size();
    }

    public int getMaxPlayerNumber(){
        return maxPlayerNumber;
    }

    // Launched when create game button is pressed
    public void startGame(Game game){
        game.startGame();
    }

    // Launched when create game button is pressed
    public void startGame() {

    }
    public boolean askPlayer(Player player, Riddle riddle){
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
