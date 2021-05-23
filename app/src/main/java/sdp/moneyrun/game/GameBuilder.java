package sdp.moneyrun.game;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import sdp.moneyrun.map.Coin;
import sdp.moneyrun.map.Riddle;
import sdp.moneyrun.player.Player;

public class GameBuilder {
    @Nullable
    private String name;
    @Nullable
    private Player host;
    @Nullable
    private List<Player> players;
    private int maxPlayerCount = 0;
    @Nullable
    private List<Coin> coins;
    @Nullable
    private List<Riddle> riddles;
    @Nullable
    private Location startLocation;
    private boolean isVisible = true;

    private static final int DEFAULT_NUM_COINS = 1;
    private static final int DEFAULT_RADIUS = 10;
    private static final int DEFAULT_DURATION = 60;

    /**
     * Empty constructor
     */
    public GameBuilder() {
    }

    /**
     * @param name the game name
     */
    @NonNull
    public GameBuilder setName(@Nullable String name) {
        if (name == null) {
            throw new IllegalArgumentException("name should not be null.");
        }

        this.name = name;
        return this;
    }

    /**
     * @param host the game host
     */
    @NonNull
    public GameBuilder setHost(@Nullable Player host) {
        if (host == null) {
            throw new IllegalArgumentException("host should not be null.");
        }

        this.host = host;
        return this;
    }

    /**
     * @param players the players in the game
     */
    @NonNull
    public GameBuilder setPlayers(@Nullable List<Player> players) {
        if (players == null) {
            throw new IllegalArgumentException("players should not be null.");
        }
        if (players.isEmpty()) {
            throw new IllegalArgumentException("players can never be empty (There should always be the host)");
        }

        this.players = players;
        return this;
    }

    /**
     * @param maxPlayerCount the maximum number of players in the game
     */
    @NonNull
    public GameBuilder setMaxPlayerCount(int maxPlayerCount) {
        if (maxPlayerCount <= 0) {
            throw new IllegalArgumentException("max player count should be greater than 0.");
        }

        this.maxPlayerCount = maxPlayerCount;
        return this;
    }

    /**
     * @param coins the coins located in the map for the game
     */
    @NonNull
    public GameBuilder setCoins(@Nullable List<Coin> coins) {
        if (coins == null) {
            throw new IllegalArgumentException("coins should not be null.");
        }

        this.coins = coins;
        return this;
    }

    /**
     * @param riddles the game riddles
     */
    @NonNull
    public GameBuilder setRiddles(@Nullable List<Riddle> riddles) {
        if (riddles == null) {
            throw new IllegalArgumentException("riddles should not be null.");
        }

        this.riddles = riddles;
        return this;
    }

    /**
     * @param startLocation the start location of the game
     */
    @NonNull
    public GameBuilder setStartLocation(@Nullable Location startLocation) {
        if (startLocation == null) {
            throw new IllegalArgumentException("start location should not be null.");
        }

        this.startLocation = startLocation;
        return this;
    }

    /**
     * @param isVisible The visibility of the game in the join list
     */
    @NonNull
    public GameBuilder setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
        return this;
    }

    public Game build() {
        checkBuildArguments();
        Game game;

        if (riddles == null ) {
            game = new Game(name, host, players, maxPlayerCount, startLocation, isVisible, coins, DEFAULT_NUM_COINS, DEFAULT_RADIUS, DEFAULT_DURATION);
        }else{
            game = new Game(name, host, maxPlayerCount, riddles, coins, startLocation, isVisible, DEFAULT_NUM_COINS, DEFAULT_RADIUS, DEFAULT_DURATION);

            if (players != null) {
                game.setPlayers(players, true);
            }
        }

        return game;
    }

    public void checkBuildArguments(){

        if (name == null)
            throw new IllegalStateException("name should not be null.");

        if (host == null)
            throw new IllegalStateException("host should not be null.");

        if (maxPlayerCount <= 0)
            throw new IllegalStateException("max player count should be greater than 0.");

        if (coins == null)
            throw new IllegalStateException("coins should not be null.");

        if (startLocation == null)
            throw new IllegalStateException("start location should not be null.");

        if (riddles == null && players == null)
            throw new IllegalStateException("players and riddles should not be null.");

    }
}
