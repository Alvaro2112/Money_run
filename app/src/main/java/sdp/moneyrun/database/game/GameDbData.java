package sdp.moneyrun.database.game;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sdp.moneyrun.map.Coin;
import sdp.moneyrun.player.Player;


/**
 * This class only holds attributes THAT WILL BE ON THE DB, so
 * do not add any temporary or auxiliary attributes to it if
 * there is no use for them to be in the DB
 */
public final class GameDbData {
    boolean isVisible;
    boolean isDeleted;
    boolean started;
    boolean ended;
    @Nullable
    private String name;
    @Nullable
    private Player host;
    @Nullable
    private List<Player> players;
    @Nullable
    private List<Coin> coins;
    private int maxPlayerCount;
    @Nullable
    private Location startLocation;
    private int numCoins;
    private double radius;
    private double duration;
    private long start_time = 0;


    public GameDbData(@Nullable String name,
                      @Nullable Player host,
                      @Nullable List<Player> players,
                      int maxPlayerCount,
                      @Nullable Location startLocation,
                      boolean isVisible,
                      @NonNull List<Coin> coins) {
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
            throw new IllegalArgumentException("maxPlayerCount should be greater than 0.");
        }
        this.coins = new ArrayList<>(coins);
        this.name = name;
        this.host = host;
        this.players = players;
        this.maxPlayerCount = maxPlayerCount;
        this.startLocation = startLocation;
        this.isVisible = isVisible;
        this.isDeleted = false;
        this.started = false;
        this.ended = false;
        start_time = System.currentTimeMillis() / 1000;
    }

    public GameDbData(@Nullable String name,
                      @Nullable Player host,
                      @Nullable List<Player> players,
                      int maxPlayerCount,
                      @Nullable Location startLocation,
                      boolean isVisible,
                      @NonNull List<Coin> coins,
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
            throw new IllegalArgumentException("maxPlayerCount should be greater than 0.");
        }
        if (numCoins < 0) {
            throw new IllegalArgumentException("maxPlayerCount should be greater than 0.");
        }
        if (radius <= 0) {
            throw new IllegalArgumentException("maxPlayerCount should be greater than 0.");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("maxPlayerCount should be greater than 0.");
        }

        this.coins = new ArrayList<>(coins);
        this.name = name;
        this.host = host;
        this.players = players;
        this.maxPlayerCount = maxPlayerCount;
        this.startLocation = startLocation;
        this.isVisible = isVisible;
        this.isDeleted = false;
        this.started = false;
        this.ended = false;
        this.radius = radius;
        this.numCoins = numCoins;
        this.duration = duration;
        start_time = System.currentTimeMillis() / 1000;
    }


    public GameDbData(@Nullable GameDbData other) {
        if (other == null) {
            throw new IllegalArgumentException("other should not be null.");
        }
        this.name = other.name;
        this.host = other.host;
        this.players = other.players;
        this.maxPlayerCount = other.maxPlayerCount;
        this.startLocation = other.startLocation;
        this.isVisible = other.isVisible;
        this.coins = other.coins;
        this.isDeleted = false;
        this.started = other.started;
        this.ended = other.ended;
        this.duration = other.duration;
        this.radius = other.radius;
        this.numCoins = other.numCoins;
        this.start_time = other.start_time;
    }

    public GameDbData() {
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public Player getHost() {
        return host;
    }

    public boolean getStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public long getStartTime() {
        return start_time;
    }

    public void setStartTime(long start_time) {
        this.start_time = start_time;
    }

    public void setStartLocation(Location startLocation){ this.startLocation = startLocation;}

    public boolean getEnded() {
        return ended;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    @NonNull
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * Set List of Players
     *
     * @param players List of Players to set
     * @throws IllegalArgumentException if the Player List is empty
     */
    public void setPlayers(@Nullable List<Player> players) {
        if (players == null) {
            throw new IllegalArgumentException("players should not be null.");
        }
        if (players.isEmpty()) {
            throw new IllegalArgumentException("players should not be empty.");
        }

        this.players = new ArrayList<>(players);
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    @Nullable
    public Location getStartLocation() {
        return startLocation;
    }

    public int getNumCoins() {
        return numCoins;
    }

    public void setNumCoins(int numCoins) {
        this.numCoins = numCoins;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    @Nullable
    public List<Coin> getCoins() {
        return new ArrayList<>(coins);
    }

    public void setCoins(@Nullable List<Coin> coins) {
        if (coins == null) throw new IllegalArgumentException();
        this.coins = new ArrayList<>(coins);
    }

    public boolean setCoin(int index, @Nullable Coin coin) {
        if (index < 0 || coin == null) throw new IllegalArgumentException();
        if (coins.size() < index + 1) return false;
        coins.set(index, coin);
        return true;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean b) {
        this.isDeleted = b;
    }

    public boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(boolean b) {
        isVisible = b;
    }

    /**
     * Adds a Player to the Player List, or does nothing if already present
     *
     * @param player Player to add
     * @throws IllegalArgumentException if List already full
     */
    public void addPlayer(@Nullable Player player) {
        if (player == null) {
            throw new IllegalArgumentException("player should not be null.");
        }
        if (players.size() == maxPlayerCount) {
            throw new IllegalArgumentException("You have already attained maxPlayerCount.");
        }

        if (!players.contains(player)) {
            players.add(player);
        }
    }

    /**
     * Removes a Player from the Player List
     *
     * @param player Player to remove
     * @throws IllegalArgumentException if only one player left in List (and removing them would cause it to be empty)
     */
    public void removePlayer(@Nullable Player player) {
        if (player == null) {
            throw new IllegalArgumentException("player should not be null");
        }
        if (players.size() == 1) {
            throw new IllegalArgumentException("players should not be empty");
        }

        players.remove(player);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameDbData gameDbData = (GameDbData) o;
        return name.equals(gameDbData.name) && players.equals(gameDbData.players) &&
                (maxPlayerCount == gameDbData.maxPlayerCount) &&
                (startLocation.getLongitude() == gameDbData.startLocation.getLongitude()) &&
                (startLocation.getLatitude() == gameDbData.startLocation.getLatitude());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, players, maxPlayerCount, startLocation);
    }
}



