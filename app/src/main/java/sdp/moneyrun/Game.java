package sdp.moneyrun;

import android.location.Location;

import java.util.List;


// The entirety of the game logic should be implemented in this class
public class Game {
    private List<Player> players;
    private List<Riddle> riddles;
    private Location startLocation;//TODO: check if we will use the existing or create a new class Location
    private List<Coin> coins;

    public Game(List<Player> players, List<Riddle> riddles, List<Coin> coins, Location startLocation) {
        if (players == null || riddles == null || startLocation == null || players.isEmpty() || riddles.isEmpty())
            throw new IllegalArgumentException("Null parameter passed as argument in Game constructor");

        this.players = players;
        this.riddles = riddles;
        this.startLocation = startLocation;
        this.coins = coins;
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
