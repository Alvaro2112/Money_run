package sdp.moneyrun.game;


import android.app.Activity;
import android.content.Intent;
import android.location.Location;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sdp.moneyrun.database.GameDbData;
import sdp.moneyrun.map.Coin;
import sdp.moneyrun.map.Riddle;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.game.EndGameActivity;

// The entirety of the game logic should be implemented in this class
public class Game {
    private static final String TAG = Game.class.getSimpleName();

    private final String DATABASE_GAME = "games";
    private final String DATABASE_PLAYER = "players";
    private final String DATABASE_COINS = "coins";
    private final String DATABASE_STARTED = "started";


    //Attributes
    private final GameDbData gameDbData;
    private final List<Riddle> riddles;

    //Aux variables
    private String id;
    private boolean hasBeenAdded;
    private boolean started;

    /**
     * This constructor is used to create a game that has never been added to the database.
     *
     * @param name           the game name
     * @param host           the game host
     * @param maxPlayerCount maximum player count in the game
     * @param riddles        riddles of the game
     * @param coins          coins in the game
     * @param startLocation  location of the game
     * @param isVisible      visibility of game in the list
     */
    public Game(String name,
                Player host,
                int maxPlayerCount,
                List<Riddle> riddles,
                List<Coin> coins,
                Location startLocation,
                boolean isVisible) {
        if (name == null) {
            throw new IllegalArgumentException("name should not be null.");
        }
        if (host == null) {
            throw new IllegalArgumentException("host should not be null.");
        }
        if (riddles == null) {
            throw new IllegalArgumentException("riddles should not be null.");
        }
        if (coins == null) {
            throw new IllegalArgumentException("coins should not be null.");
        }
        if (startLocation == null) {
            throw new IllegalArgumentException("startLocation should not be null.");
        }
        if (maxPlayerCount <= 0) {
            throw new IllegalArgumentException("maxPlayerCount should not be smaller than 1.");
        }

        this.id = null;
        this.hasBeenAdded = false;
        ArrayList<Player> players = new ArrayList<>();
        players.add(host);
        this.gameDbData = new GameDbData(name, host, players, maxPlayerCount, startLocation, isVisible, coins);
        this.riddles = riddles;
        started = false;
    }

    /**
     * This constructor is used to create an instance of game from retrieved information
     * from database.
     *
     * @param name           the game name
     * @param host           the game host
     * @param players        players in the game
     * @param maxPlayerCount max player count in the game
     * @param startLocation  location of the game
     * @param isVisible      visibility of game in the list
     */
    public Game(String name,
                Player host,
                List<Player> players,
                int maxPlayerCount,
                Location startLocation,
                boolean isVisible,
                List<Coin> coins) {
        if (name == null) {
            throw new IllegalArgumentException("name should not be null.");
        }
        if (host == null) {
            throw new IllegalArgumentException("host should not be null.");
        }
        if (players == null) {
            throw new IllegalArgumentException("players should not be null.");
        }
        if (startLocation == null) {
            throw new IllegalArgumentException("startLocation should not be null.");
        }
        if (maxPlayerCount <= 0) {
            throw new IllegalArgumentException("maxPlayerCount should not be smaller than 1.");
        }
        if (coins == null) {
            throw new IllegalArgumentException("coins should not be null.");
        }

        this.id = null;
        this.hasBeenAdded = false;

        this.gameDbData = new GameDbData(name, host, players, maxPlayerCount, startLocation, isVisible, coins);
        this.riddles = new ArrayList<>();
        started = false;

    }

    public static void endGame(int numberOfCollectedCoins, int score, int playerId, Activity currentActivity) {

        Intent endGameIntent = new Intent(currentActivity, EndGameActivity.class);
        ArrayList<Integer> collectedCoinsValues = new ArrayList<>();
        endGameIntent.putExtra("numberOfCollectedCoins", numberOfCollectedCoins);
        endGameIntent.putExtra("score", score);
        endGameIntent.putExtra("playerId", playerId);
        currentActivity.startActivity(endGameIntent);
        currentActivity.finish();
    }

    public static void startGame(Game game) {
        game.startGame();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id should not be null.");
        }

        this.id = id;
    }

    public String getName() {
        return gameDbData.getName();
    }

    public Player getHost() {
        return gameDbData.getHost();
    }

    public int getMaxPlayerCount() {
        return gameDbData.getMaxPlayerCount();
    }

    public List<Player> getPlayers() {
        return gameDbData.getPlayers();
    }

    public boolean getStarted() {
        return gameDbData.getStarted();
    }

    public void setStarted(boolean started, boolean forceLocal) {
        gameDbData.setStarted(started);
        if(!forceLocal){
            FirebaseDatabase.getInstance().getReference()
                    .child(DATABASE_GAME)
                    .child(id)
                    .child(DATABASE_STARTED)
                    .setValue(started);
        }
    }

    public List<Riddle> getRiddles() {
        return new ArrayList<>(riddles);
    }

    public List<Coin> getCoins() {
        return new ArrayList<>(gameDbData.getCoins());
    }

    public void setCoins(List<Coin> coins, boolean forceLocal) {
        if (coins == null) throw new IllegalArgumentException();
        gameDbData.setCoins(coins);

        if(!forceLocal)
        {
            FirebaseDatabase.getInstance().getReference()
                    .child(DATABASE_GAME)
                    .child(id)
                    .child(DATABASE_COINS)
                    .setValue(coins);
        }

    }

    public Location getStartLocation() {
        return gameDbData.getStartLocation();
    }

    public boolean getIsVisible() {
        return gameDbData.getIsVisible();
    }

    public int getPlayerCount() {
        return gameDbData.getPlayers().size();
    }

    public boolean getHasBeenAdded() {
        return hasBeenAdded;
    }

    public void setHasBeenAdded(boolean hasBeenAdded) {
        this.hasBeenAdded = hasBeenAdded;
    }

    public GameDbData getGameDbData() {
        return new GameDbData(gameDbData);
    }

    /**
     * Sets the players for the Game, or for both the Game and the DB if it has been added
     *
     * @param players    New List of Players
     * @param forceLocal force the modification to be local only
     */
    public void setPlayers(List<Player> players, boolean forceLocal) {
        if (players == null) {
            throw new IllegalArgumentException("players should not be null.");
        }
        if (players.isEmpty()) {
            throw new IllegalArgumentException("Player List can never be empty (There should always be the host)");
        }

        if (!hasBeenAdded || forceLocal) {
            gameDbData.setPlayers(players);
        } else {
            gameDbData.setPlayers(players);
            FirebaseDatabase.getInstance().getReference()
                    .child(DATABASE_GAME)
                    .child(id)
                    .child(DATABASE_PLAYER)
                    .setValue(players);
        }
    }

    /**
     * Add a player to the game, updates it in the database if necessary
     *
     * @param player new player
     */
    public void addPlayer(Player player, boolean forceLocal) {
        if (player == null) {
            throw new IllegalArgumentException("player should not be null.");
        }
        if (getPlayers().contains(player)) {
            return;
        }

        List<Player> players = getPlayers();
        players.add(player);

        setPlayers(players, forceLocal);
    }

    public boolean setCoin(int index, Coin coin) {
        if (index < 0 || coin == null) throw new IllegalArgumentException();
        return gameDbData.setCoin(index, coin);
    }

    /**
     * Remove a player to the game, updates it in the database if necessary
     *
     * @param player the player to be removed
     * @return the player previously at the specified location
     */
    public Player removePlayer(Player player, boolean forceLocal) {
        if (player == null) {
            throw new IllegalArgumentException("player should not be null.");
        }
        if (!getPlayers().contains(player)) {
            return null;
        }

        List<Player> players = getPlayers();
        players.remove(player);

        setPlayers(players, forceLocal);

        return player;
    }

    // Launched when create game button is pressed
    public void startGame() {
    }

    public boolean askPlayer(Player player, Riddle riddle) {
        String playerResponse = player.ask(riddle.getQuestion());
        return playerResponse.trim().replaceAll(" ", "").toLowerCase().equals(riddle.getAnswer());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return gameDbData.equals(game.gameDbData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameDbData);
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
