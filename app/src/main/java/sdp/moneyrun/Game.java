package sdp.moneyrun;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;


// The entirety of the game logic should be implemented in this class
public class Game {
    private final String gameId;
    private final String name;
    private final List<Player> players;
    private final int maxPlayerCount;
    private final List<Riddle> riddles;
    private final Location startLocation; //TODO: check if we will use the existing or create a new class Location
    private final List<Coin> coins;
    private boolean isVisible;

    public Game(String gameId,
                String name,
                List<Player> players,
                int maxPlayerCount,
                List<Riddle> riddles,
                List<Coin> coins,
                Location startLocation) {
        if(gameId == null){
            throw new IllegalArgumentException("Game id should not be null.");
        }
        if(name == null){
            throw new IllegalArgumentException("Game name should not be null.");
        }
        if(players == null){
            throw new IllegalArgumentException("Players should not be null.");
        }
        if(riddles == null){
            throw new IllegalArgumentException("Riddles should not be null.");
        }
        if(coins == null){
            throw new IllegalArgumentException("Coins should not be null.");
        }
        if(startLocation  == null){
            throw new IllegalArgumentException("Start location should not be null.");
        }

        this.isVisible = true;
        this.gameId = gameId;
        this.name = name;
        this.players = players;
        this.maxPlayerCount = maxPlayerCount;
        this.riddles = riddles;
        this.coins = coins;
        this.startLocation = startLocation;
    }

    public Game(String gameId,
                String name,
                boolean isVisible,
                List<Player> players,
                int maxPlayerCount,
                List<Riddle> riddles,
                List<Coin> coins,
                Location startLocation) {
        this(gameId, name, players, maxPlayerCount, riddles, coins, startLocation);
        this.isVisible = isVisible;
    }


    public static void endGame(List<Coin> collectedCoins, int playerId, Activity currentActivity) {
        Intent endGameIntent = new Intent(currentActivity, EndGameActivity.class);
        ArrayList<Integer> collectedCoinsValues = new ArrayList<>();
        for (int i = 0; i < collectedCoins.size(); ++i) {
            collectedCoinsValues.add(collectedCoins.get(i).getValue());
        }
        endGameIntent.putExtra("collectedCoins", collectedCoinsValues);
        endGameIntent.putExtra("playerId", playerId);
        currentActivity.startActivity(endGameIntent);
        currentActivity.finish();
    }

    public String getGameId() {
        return gameId;
    }

    public String getName() {
        return name;
    }


    public boolean getIsVisible(){
        return isVisible;
    }

    public int getPlayerCount(){
        return players.size();
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public void setIsVisible(boolean isVisible){
        this.isVisible = isVisible;
    }

    // Launched when create game button is pressed
    public void startGame(){}

    public static void startGame(Game game){
        game.startGame();
    }

    public boolean askPlayer(Player player, Riddle riddle){
        String playerResponse = player.ask(riddle.getQuestion());
        return playerResponse.trim().replaceAll(" ", "").toLowerCase().equals(riddle.getAnswer());
    }

    /**
     * @return returns a random riddle from all the possible riddles
     */
    public Riddle getRandomRiddle() {

        if (riddles.isEmpty()) {
            return null;
        }

        int index = (int) (Math.random() * (riddles.size()));
        return riddles.get(index);
    }
}
