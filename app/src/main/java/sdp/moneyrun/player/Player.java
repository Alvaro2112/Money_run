package sdp.moneyrun.player;

import java.io.Serializable;
import java.util.Objects;

import sdp.moneyrun.database.DatabaseProxy;

public class Player implements Serializable {

    private int playerId;
    private String name;
    private String address;
    private int numberOfPlayedGames;
    private int numberOfDiedGames;
    private int totalDistanceRun;
    private int maxScoreInGame;
    private String preferredColor;
    private String preferredPet;
    private int score;

    /**
     * For database purpose, a default constructor is needed
     */
    public Player(){}

    public Player(int playerId){
        this.playerId = playerId;
    }


    /**
     * Constructor, returns instance of player
     * @param playerId the unique id that identifies a player
     * @param name
     * @param address
     * @param numberOfDiedGames
     * @param numberOfPlayedGames
     * @throws IllegalArgumentException on empty or null address or name and on player = 0
     */
    public Player(int playerId, String name, String address, int numberOfDiedGames,
                  int numberOfPlayedGames, int score){
        if (playerId == 0 || name == null || name.isEmpty() || address == null ||address.isEmpty())
            throw new IllegalArgumentException();
        this.playerId = playerId;
        this.name = name;
        this.address = address;
        this.numberOfDiedGames = numberOfDiedGames;
        this.numberOfPlayedGames = numberOfPlayedGames;
        this.score = score;
    }


    //TODO This constructor should be removed once @Tesa fixes the merge error he created
    public Player(int playerId, String name, String address, int numberOfDiedGames,
                  int numberOfPlayedGames){
        if (playerId == 0 || name == null || name.isEmpty() || address == null ||address.isEmpty())
            throw new IllegalArgumentException();
        this.playerId = playerId;
        this.name = name;
        this.address = address;
        this.numberOfDiedGames = numberOfDiedGames;
        this.numberOfPlayedGames = numberOfPlayedGames;
    }

    /**
     * Setter for name. By design the player already had a name
     * @param name
     * @param dbChange whether the database entry must be updated
     */
    public void setName(String name, boolean dbChange) {
        this.name = name;
        dbUpdate(dbChange);
    }

    /**
     * Setter without db change
     * @param name
     */
    public void setName(String name){
        this.setName(name, false);
    }

    /**
     *Setter for address. By design the player already had an address
     * @param address
     * @param dbChange whether the database entry must be updated
     */
    public void setAddress(String address, boolean dbChange) {
        this.address = address;
       dbUpdate(dbChange);
    }

    /**
     * Setter without db change
     * @param address
     */
    public void setAddress(String address){
        this.setAddress(address, false);
    }

    /**
     * Increments the number of played games
     */
    public void updatePlayedGames(){
        this.updatePlayedGames(false);
    }

    /**
     * Increments the number of played games
     * @param dbChange whether the database entry must be updated
     */
    public void updatePlayedGames(boolean dbChange) {
        numberOfPlayedGames++;
        dbUpdate(dbChange);
    }

    /**
     * Increments the number of died games
     */
    public void updateDiedGames(){
        this.updateDiedGames(false);
    }

    /**
     * Increments the number of died games
     * @param dbChange whether the database entry must be updated
     */
    public void updateDiedGames(boolean dbChange) {
        numberOfDiedGames++;
        dbUpdate(dbChange);
    }

    /**
     * sets the number of died games
     * @param diedGames
     * @param dbChange whether the database entry must be updated
     */
    public void setNumberOfDiedGames (int diedGames, boolean dbChange){
        numberOfDiedGames = diedGames;
        dbUpdate(dbChange);
    }

    /**
     * sets the number of died games
     * @param diedGames
     */
    public void setNumberOfDiedGames (int diedGames){
        this.setNumberOfDiedGames(diedGames, false);
    }

    /**
     * sets the number of played games
     * @param playedGames
     * @param dbChange
     */
    public void setNumberOfPlayedGames(int playedGames, boolean dbChange){
        numberOfPlayedGames = playedGames;
        dbUpdate(dbChange);
    }

    /**
     * sets the number of played games
     * @param playedGames
     */
    public void setNumberOfPlayedGames(int playedGames){
        this.setNumberOfPlayedGames(playedGames, false);
    }


    /**
     * sets the score
     * @param score the score that is to update
     * @param dbChange whether the database entry must be updated
     */
    public void setScore(int score, boolean dbChange){
        this.score = score;
        dbUpdate(dbChange);
    }

    /**
     * sets the score
     * @param score
     */
    public void setScore(int score){
        this.setScore(score, false);
    }


        /**
         *
         * @return number of games in which the player died
         */
    public int getNumberOfDiedGames() {
        return numberOfDiedGames;
    }

    /**
     *
     * @return the unique player id
     */
    public int getPlayerId() {
        return playerId;
    }


    /**
     *
     * @return the score of that player
     */
    public int getScore(){
        return score;
    }


    /**
     *
     * @return the adress of the player
     */
    public String getAddress() {
        return address;
    }

    /**
     *
     * @return the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return the number of games the player played
     */
    public int getNumberOfPlayedGames() {
        return numberOfPlayedGames;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return playerId == player.playerId &&
                numberOfPlayedGames == player.numberOfPlayedGames &&
                numberOfDiedGames == player.numberOfDiedGames &&
                name.equals(player.name) &&
                address.equals(player.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, name, address, numberOfPlayedGames, numberOfDiedGames);
    }

    /**
     * Updates player values in the firebase database
     * @param dbChange
     */
    private void dbUpdate(boolean dbChange){
        if(dbChange){
            DatabaseProxy db = new DatabaseProxy();
            db.putPlayer(this);
        }
    }


    /**
     *
     * @param question
     * @return the answer of the question asked
     */
    public String ask(String question){
        String answer = "";
        //TODO: display question on  player's screen and store the response
        return answer;
    }
    //TODO: add later methods related to the game itself
}
