package sdp.moneyrun;

import java.util.Objects;

public class Player {

    private  int playerId;
    private String name;
    private String address;
    private int numberOfPlayedGames;
    private int numberOfDiedGames;
    private int totalDistanceRun;
    private int maxScoreInGame;
    private String preferredColor;
    private String preferredPet;
    private int score;

    /*
    For database purpose, a default constructor is needed
     */
    public Player(){}


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
                  int numberOfPlayedGames){
        if (playerId == 0 || name == null || name.isEmpty() || address == null ||address.isEmpty())
            throw new IllegalArgumentException();
        this.playerId = playerId;
        this.setName(name);
        this.setAddress(address);
        this.numberOfDiedGames = numberOfDiedGames;
        this.numberOfPlayedGames = numberOfPlayedGames;
    }

    /**
     * Setter for name. By design the player already had a name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *Setter for address. By design the player already had an address
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Increments the number of played games
     */
    public void updatePlayedGames() {
        numberOfPlayedGames++;
    }

    /**
     * Increments the number of died games
     */
    public void updateDiedGames() {
        numberOfDiedGames++;
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
        if (name == null)
            throw new IllegalStateException();

        return score;
    }

    /**
     *
     * @param score the score that is to update
     */
    public void setScore(int score){
        this.score = score;
    }


    /**
     *
     * @return the adress of the player
     */
    public String getAddress() {
        if (address == null)
            throw new IllegalStateException();
        return address;
    }

    /**
     *
     * @return the name of the player
     */
    public String getName() {
        if (name == null)
            throw new IllegalStateException();
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
