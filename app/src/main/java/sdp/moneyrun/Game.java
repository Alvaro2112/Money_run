package sdp.moneyrun;

import android.location.Location;




import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


// The entirety of the game logic should be implemented in this class
public class Game {
    private final String gameId;
    private final String name;
    private List<Player> players;
    private int playerCount;
    private int maxPlayerCount;
    private List<Riddle> riddles;
    private Location startLocation;//TODO: check if we will use the existing or create a new class Location
    private List<Coin> coins;

    public Game(String gameId, String name, List<Player> players, List<Riddle> riddles, List<Coin> coins, Location startLocation) {
        if (players == null || riddles == null || startLocation == null)
            throw new IllegalArgumentException("Null parameter passed as argument in Game constructor");
        this.gameId = gameId;
        this.name = name;
        this.players = players;
        this.riddles = riddles;
        this.startLocation = startLocation;
        this.coins = coins;
    }

    public String getGameId(){
        return gameId;
    }

    public String getName(){
        return name;
    }

    public int getPlayerCount(){
        return players.size();
    }

    public int getMaxPlayerCount(){
        return maxPlayerCount;
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

    /**
     *
     * @return returns a random riddle from all the possible riddles
     */
    public Riddle getRandomRiddle(){

        if(riddles.isEmpty()){
            return null;
        }

        int index = (int)(Math.random() * (riddles.size()));
        return riddles.get(index);
    }


}
