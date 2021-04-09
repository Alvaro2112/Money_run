package sdp.moneyrun;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    //private List<Riddle> riddles;
    private Location startLocation;//TODO: check if we will use the existing or create a new class Location
    //TODO add Game Host Attribute and change setPlayers so that it can never be empty and
    //the host always has to be in it

    public GameData(String name, List<Player> players, Integer maxPlayerNumber, List<Riddle> riddles, Location startLocation){
        if(name == null || players == null || startLocation == null) {
            throw new IllegalArgumentException("Null parameter passed as argument in Game constructor");
        }
        if(players.isEmpty()){
            throw new IllegalArgumentException("Player List must have at least one player (The host)");
        }
        if(maxPlayerNumber <= 0){
            throw new IllegalArgumentException("Error : maxPlayers <= 0");
        }
        this.name = name;
        this.players = new ArrayList<>(players);
        this.maxPlayerNumber = maxPlayerNumber;
        //this.riddles = new ArrayList<>(riddles);
        this.startLocation = new Location(startLocation);
    }


    public GameData(GameData data){
        if(data == null){throw new IllegalArgumentException();}
        this.name = data.getName();
        this.players = data.getPlayers();
        this.maxPlayerNumber = data.getMaxPlayerNumber();
      //  this.riddles = data.getRiddles();
        this.startLocation = data.getStartLocation();
    }

    public GameData(){}

    public String getName() {
        return name;
    }


    public List<Player> getPlayers() {
        return new ArrayList<>(players);

    }

    public int getMaxPlayerNumber() {
        return maxPlayerNumber;
    }

    /*public List<Riddle> getRiddles() {
        return new ArrayList<>(riddles);
    }*/

    public Location getStartLocation() {
        return new Location(startLocation);
    }

    /**
     * Adds a Player to the Player List, or does nothing if already present
     * @param p Player to add
     * @throws IllegalArgumentException if List already full
     */
    public void addPlayer(Player p){
        if(p == null){throw new IllegalArgumentException();}
        if(players.size() == maxPlayerNumber){throw new IllegalArgumentException("You have already attained MaxPlayerNumber");}
        if(!players.contains(p)){
            players.add(p);
        }
    }

    /**
     * Removes a Player from the Player List
     * @param p Player to remove
     * @throws IllegalArgumentException if only one player left in List (and removing them would cause it to be empty)
     */
    public void removePlayer(Player p){
        if(p == null){throw new IllegalArgumentException();}
        if(players.size() == 1) { throw new IllegalArgumentException("Player List can never be empty");}
        players.remove(p);
    }


    /**
     * Set List of Players
     * @param players List of Players to set
     * @throws IllegalArgumentException if the Player List is empty
     */
    public void setPlayers(List<Player> players) {
        if(players == null){throw new IllegalArgumentException();}
        if(players.isEmpty()){throw new IllegalArgumentException("Players can never be empty");}
        this.players = new ArrayList<>(players);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameData gameData = (GameData) o;
        return name.equals(gameData.name) &&
                players.equals(gameData.players) &&
                maxPlayerNumber.equals(gameData.maxPlayerNumber) &&
                startLocation.equals(gameData.startLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, players, maxPlayerNumber, startLocation);
    }
}



