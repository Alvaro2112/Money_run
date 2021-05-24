package sdp.moneyrun.game;


import android.app.Activity;
import android.content.Intent;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sdp.moneyrun.Helpers;
import sdp.moneyrun.database.GameDbData;
import sdp.moneyrun.map.Coin;
import sdp.moneyrun.map.Riddle;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.game.EndGameActivity;

// The entirety of the game logic should be implemented in this class
@SuppressWarnings("FieldCanBeLocal")
public class Game {

    private final String DATABASE_GAME = "games";
    private final String DATABASE_PLAYER = "players";
    private final String DATABASE_COINS = "coins";
    private final String DATABASE_IS_DELETED = "isDeleted";
    private final String DATABASE_IS_VISIBLE = "isVisible";
    private final String DATABASE_STARTED = "started";
    //Attributes
    @NonNull
    private final GameDbData gameDbData;
    @Nullable
    private final List<Riddle> riddles;

    //Aux variables
    @Nullable
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
    public Game(@Nullable String name,
                @Nullable Player host,
                int maxPlayerCount,
                @Nullable List<Riddle> riddles,
                @Nullable List<Coin> coins,
                @Nullable Location startLocation,
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
        this.hasBeenAdded = false;
        ArrayList<Player> players = new ArrayList<>();
        players.add(host);
        this.gameDbData = new GameDbData(name, host, players, maxPlayerCount, startLocation, isVisible, coins);
        this.riddles = riddles;
    }

    public Game(@Nullable String name,
                @Nullable Player host,
                int maxPlayerCount,
                @Nullable List<Riddle> riddles,
                @Nullable List<Coin> coins,
                @Nullable Location startLocation,
                boolean isVisible,
                int numCoins,
                double radius,
                double duration) {
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

        if (numCoins < 0) {
            throw new IllegalArgumentException("Number of coins should be bigger than 0.");
        }
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius should be bigger than 0.");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration should be bigger than 0.");
        }

        this.hasBeenAdded = false;
        ArrayList<Player> players = new ArrayList<>();
        players.add(host);
        this.gameDbData = new GameDbData(name, host, players, maxPlayerCount, startLocation, isVisible, coins, numCoins, radius, duration);
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
    public Game(@Nullable String name,
                @Nullable Player host,
                @Nullable List<Player> players,
                int maxPlayerCount,
                @Nullable Location startLocation,
                boolean isVisible,
                @Nullable List<Coin> coins) {
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

        this.hasBeenAdded = false;
        started = false;

        this.gameDbData = new GameDbData(name, host, players, maxPlayerCount, startLocation, isVisible, coins);
        this.riddles = new ArrayList<>();

    }


    public Game(@Nullable String name,
                @Nullable Player host,
                @Nullable List<Player> players,
                int maxPlayerCount,
                @Nullable Location startLocation,
                boolean isVisible,
                @Nullable List<Coin> coins,
                int numCoins,
                double radius,
                double duration) {
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

        if (numCoins < 0) {
            throw new IllegalArgumentException("Number of coins should be bigger than 0.");
        }
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius should be bigger than 0.");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration should be bigger than 0.");
        }


        this.hasBeenAdded = false;
        started = false;
        this.gameDbData = new GameDbData(name, host, players, maxPlayerCount, startLocation, isVisible, coins, numCoins, radius, duration);
        this.riddles = new ArrayList<>();

    }

    public static void endGame(int numberOfCollectedCoins, int score, String playerId,List<Player>players, @NonNull Activity currentActivity) {

        Intent endGameIntent = new Intent(currentActivity, EndGameActivity.class);
        endGameIntent.putExtra("numberOfCollectedCoins", numberOfCollectedCoins);
        endGameIntent.putExtra("score", score);
        endGameIntent.putExtra("playerId", playerId);
        endGameIntent.putExtra("numberOfPlayers",players.size());
        Helpers.putPlayersInIntent(endGameIntent,players);
        currentActivity.startActivity(endGameIntent);
        currentActivity.finish();

    }

    public boolean isStarted() {
        return started;
    }

    @Nullable
    public String getId() {
        return id;
    }

    public void setId(@Nullable String id) {
        if (id == null) {
            throw new IllegalArgumentException("id should not be null.");
        }

        this.id = id;
    }

    @Nullable
    public String getName() {
        return gameDbData.getName();
    }

    @Nullable
    public Player getHost() {
        return gameDbData.getHost();
    }


    public int getMaxPlayerCount() {
        return gameDbData.getMaxPlayerCount();
    }

    @NonNull
    public List<Player> getPlayers() {
        return gameDbData.getPlayers();
    }

    public boolean getStarted() {
        return gameDbData.getStarted();
    }

    public void setStarted(boolean started, boolean forceLocal) {
        if (!forceLocal) {
            FirebaseDatabase.getInstance().getReference()
                    .child(DATABASE_GAME)
                    .child(id)
                    .child(DATABASE_STARTED)
                    .setValue(started);
        }
        gameDbData.setStarted(started);
        this.started = started;

    }

    @Nullable
    public List<Riddle> getRiddles() {
        return new ArrayList<>(riddles);
    }

    @NonNull
    public List<Coin> getCoins() {
        return new ArrayList<>(gameDbData.getCoins());
    }

    public void setCoins(@Nullable List<Coin> coins, boolean forceLocal) {
        if (coins == null) throw new IllegalArgumentException();
        gameDbData.setCoins(coins);

        if (!forceLocal) {
            FirebaseDatabase.getInstance().getReference()
                    .child(DATABASE_GAME)
                    .child(id)
                    .child(DATABASE_COINS)
                    .setValue(coins);
        }

    }

    @Nullable
    public Location getStartLocation() {
        return gameDbData.getStartLocation();
    }

    public boolean getIsVisible() {
        return gameDbData.getIsVisible();
    }

    public void setIsVisible(boolean value, boolean forceLocal) {
        if (hasBeenAdded && !forceLocal)
            setDatabaseVariable(DATABASE_IS_VISIBLE, value);

        gameDbData.setIsVisible(value);
    }

    public boolean getIsDeleted() {
        return gameDbData.getIsDeleted();
    }

    /**
     * Set the value of isDeleted
     *
     * @param value      new value
     * @param forceLocal set to true if it is to only be done locally, false
     *                   if both database and local values should be changed
     *                   (game must still have been added to the DB for false to work)
     */
    public void setIsDeleted(boolean value, boolean forceLocal) {
        if (hasBeenAdded && !forceLocal)
            setDatabaseVariable(DATABASE_IS_DELETED, value);

        gameDbData.setIsDeleted(value);

    }

    public void setDatabaseVariable(@NonNull String variable, @Nullable Object value){
        if(value == null)
            throw new NullPointerException();

        FirebaseDatabase.getInstance().getReference()
                .child(DATABASE_GAME)
                .child(id)
                .child(variable)
                .setValue(value);
    }

    public int getPlayerCount() {
        return gameDbData.getPlayers().size();
    }

    public int getNumCoins() {
        return gameDbData.getNumCoins();
    }

    public double getRadius() {
        return gameDbData.getRadius();
    }

    public double getDuration() {
        return gameDbData.getDuration();
    }

    public boolean getHasBeenAdded() {
        return hasBeenAdded;
    }

    public void setHasBeenAdded(boolean hasBeenAdded) {
        this.hasBeenAdded = hasBeenAdded;
    }

    @NonNull
    public GameDbData getGameDbData() {
        return new GameDbData(gameDbData);
    }

    /**
     * Sets the players for the Game, or for both the Game and the DB if it has been added
     *
     * @param players    New List of Players
     * @param forceLocal force the modification to be local only
     */
    public void setPlayers(@Nullable List<Player> players, boolean forceLocal) {
        if (players == null) {
            throw new IllegalArgumentException("players should not be null.");
        }
        if (players.isEmpty()) {
            throw new IllegalArgumentException("Player List can never be empty (There should always be the host)");
        }

        gameDbData.setPlayers(players);

        if (hasBeenAdded && !forceLocal)
            setDatabaseVariable(DATABASE_PLAYER, players);

    }

    public void setPlayers(List<Player> players, boolean forceLocal, OnCompleteListener listener) {
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
                    .setValue(players).addOnCompleteListener(listener);
        }
    }

    /**
     * Add a player to the game, updates it in the database if necessary
     *
     * @param player new player
     */
    public void addPlayer(@Nullable Player player, boolean forceLocal) {
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

    public boolean setCoin(int index, @Nullable Coin coin) {
        if (index < 0 || coin == null) throw new IllegalArgumentException();
        return gameDbData.setCoin(index, coin);
    }

    /**
     * Remove a player to the game, updates it in the database if necessary
     *
     * @param player the player to be removed
     */
    public void removePlayer(@Nullable Player player, boolean forceLocal) {
        if (player == null) {
            throw new IllegalArgumentException("player should not be null.");
        }
        if (!getPlayers().contains(player)) {
            return;
        }

        List<Player> players = getPlayers();
        players.remove(player);

        setPlayers(players, forceLocal);

    }

    @Override
    public boolean equals(@Nullable Object o) {
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
    @Nullable
    public Riddle getRandomRiddle() {

        if (riddles.isEmpty()) {
            return null;
        }

        int index = (int) (Math.random() * (riddles.size()));
        return riddles.get(index);
    }
}
