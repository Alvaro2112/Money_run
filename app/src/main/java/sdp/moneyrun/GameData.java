package sdp.moneyrun;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.Player;
import sdp.moneyrun.Riddle;


/*
This class only holds data fundamental for the game, all the attributes of this class
WILL be in the database, so do not add any temporary or auxiliary attributes to it if
there is no use for them to be in the DB
 */
public final class GameData {
    private String name;
    private List<Player> players;
    private Integer maxPlayerNumber;
    private List<Riddle> riddles;
    private Location startLocation;//TODO: check if we will use the existing or create a new class Location
    private List<Coin> coins;


    public GameData(String name, List<Player> players, Integer maxPlayerNumber, List<Riddle> riddles, Location startLocation, List<Coin> coins){
        if(name == null || players == null || riddles == null || startLocation == null || coins == null) {
            throw new IllegalArgumentException("Null parameter passed as argument in Game constructor");
        }
        this.name = name;
        this.players = new ArrayList<>(players);
        this.maxPlayerNumber = maxPlayerNumber;
        this.riddles = new ArrayList<>(riddles);
        this.startLocation = new Location(startLocation);
    }


    public GameData(GameData data){
        if(data == null){throw new IllegalArgumentException();}
        this.name = data.getName();
        this.players = data.getPlayers();
        this.maxPlayerNumber = data.getMaxPlayerNumber();
        this.riddles = data.getRiddles();
        this.startLocation = data.getStartLocation();
    }

    public GameData(){}

    public String getName() {
        return name;
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public Integer getMaxPlayerNumber() {
        return maxPlayerNumber;
    }

    public List<Riddle> getRiddles() {
        return new ArrayList<>(riddles);
    }

    public Location getStartLocation() {
        return new Location(startLocation);
    }

    public void setPlayers(List<Player> players) {
        if(players == null){throw new IllegalArgumentException();}
        this.players = new ArrayList<>(players);
    }

    public List<Coin> getCoins() {
        return new ArrayList<>(coins);
    }

}



