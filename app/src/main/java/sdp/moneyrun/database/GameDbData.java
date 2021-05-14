package sdp.moneyrun.database;

import android.location.Location;

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
    private String name;
    private Player host;
    private List<Player> players;
    private List<Coin> coins;
    //TODO add Game Host Attribute and change setPlayers so that it can never be empty and
    private int maxPlayerCount;
    private Location startLocation;
    boolean isVisible;
    boolean isDeleted;
    boolean isStarted;
    private int numCoins;
    private double radius;
    private double duration;

    public GameDbData(String name,
                      Player host,
                      List<Player> players,
                      int maxPlayerCount,
                      Location startLocation,
                      boolean isVisible,
                      List<Coin> coins){
        if(name == null){
            throw new IllegalArgumentException("name should not be null.");
        }
        if(host == null){
            throw new IllegalArgumentException("host should not be null.");
        }
        if(players == null){
            throw new IllegalArgumentException("players should not be null.");
        }
        if(startLocation == null){
            throw new IllegalArgumentException("startLocation should not be null.");
        }
        if(maxPlayerCount <= 0){
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
        this.isStarted = false;
    }

    public GameDbData(String name,
                      Player host,
                      List<Player> players,
                      int maxPlayerCount,
                      Location startLocation,
                      boolean isVisible,
                      List<Coin> coins,
                      int numCoins,
                      double radius,
                      double duration){
        if(name == null){
            throw new IllegalArgumentException("name should not be null.");
        }
        if(host == null){
            throw new IllegalArgumentException("host should not be null.");
        }
        if(players == null){
            throw new IllegalArgumentException("players should not be null.");
        }
        if(startLocation == null){
            throw new IllegalArgumentException("startLocation should not be null.");
        }
        if(maxPlayerCount <= 0){
            throw new IllegalArgumentException("maxPlayerCount should be greater than 0.");
        }
        if(numCoins <= 0){
            throw new IllegalArgumentException("maxPlayerCount should be greater than 0.");
        }
        if(radius <= 0){
            throw new IllegalArgumentException("maxPlayerCount should be greater than 0.");
        }
        if(duration <= 0){
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
        this.isStarted = false;
        this.radius = radius;
        this.numCoins = numCoins;
        this.duration = duration;
    }


    public GameDbData(GameDbData other){
        if(other == null){
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
        this.isStarted = other.isStarted;
    }

    public GameDbData(){}

    public String getName() {
        return name;
    }

    public Player getHost(){
        return host;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public int getNumCoins(){return numCoins;}
    public  double getRadius(){return  radius;}
    public double getDuration(){return duration;}

    public List<Coin> getCoins() {
        return new ArrayList<>(coins);
    }

    public boolean setCoin(int index, Coin coin){
        if(index < 0  || coin == null) throw new IllegalArgumentException();
        if( coins.size() < index + 1) return false;
        coins.set(index, coin);
        return true;
    }

    public void setIsDeleted(boolean b){
        this.isDeleted = b;
    }

    public boolean getIsDeleted(){
        return isDeleted;
    }

    public void setIsVisible(boolean b){isVisible = b;}


    public boolean getIsVisible(){
        return isVisible;
    }

    /**
     * Adds a Player to the Player List, or does nothing if already present
     * @param player Player to add
     * @throws IllegalArgumentException if List already full
     */
    public void addPlayer(Player player){
        if(player == null){
            throw new IllegalArgumentException("player should not be null.");
        }
        if(players.size() == maxPlayerCount){
            throw new IllegalArgumentException("You have already attained maxPlayerCount.");
        }

        if(!players.contains(player)){
            players.add(player);
        }
    }

    /**
     * Removes a Player from the Player List
     * @param player Player to remove
     * @throws IllegalArgumentException if only one player left in List (and removing them would cause it to be empty)
     */
    public void removePlayer(Player player){
        if(player == null){
            throw new IllegalArgumentException("player should not be null");
        }
        if(players.size() == 1) {
            throw new IllegalArgumentException("players should not be empty");
        }

        players.remove(player);
    }

    /**
     * Set List of Players
     * @param players List of Players to set
     * @throws IllegalArgumentException if the Player List is empty
     */
    public void setPlayers(List<Player> players) {
        if(players == null){
            throw new IllegalArgumentException("players should not be null.");
        }
        if(players.isEmpty()){
            throw new IllegalArgumentException("players should not be empty.");
        }

        this.players = new ArrayList<>(players);
    }

    public void setCoins(List<Coin> coins) {
        if(coins == null) throw new IllegalArgumentException();
        this.coins = new ArrayList<>(coins);
    }



    @Override
    public boolean equals(Object o) {
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



