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
    private List<Player> players;
    private List<Riddle> riddles;
    private Location startLocation;//TODO: check if we will use the existing or create a new class Location

    public Game(List<Player> players, List<Riddle> riddles, Location startLocation ){
        if(players == null || riddles == null || startLocation == null || players.isEmpty() || riddles.isEmpty())
            throw new IllegalArgumentException("Null parameter passed as argument in Game constructor");
        this.players = players;
        this.riddles = riddles;
        this.startLocation = startLocation;
    }

    // Launched when create game button is pressed
    public void startGame(){

    }
    public static void startGame(Game game){
        game.startGame();
    }
    public boolean askPlayer(Player player, Riddle riddle){
        String playerResponse = player.ask(riddle.getQuestion());
        return playerResponse.trim().replaceAll(" ", "").toLowerCase().equals(riddle.getAnswer());
    }


}
