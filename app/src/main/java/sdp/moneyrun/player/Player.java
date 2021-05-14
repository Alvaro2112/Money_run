package sdp.moneyrun.player;

import java.io.Serializable;
import java.util.Objects;

import sdp.moneyrun.database.PlayerDatabaseProxy;

public class Player implements Serializable {

    private String playerId;
    private String name;
    private int score;

    /**
     * For database purpose, a default constructor is needed
     */
    public Player() {
    }

    public Player(String playerId) {
        this.playerId = playerId;
    }


    /**
     * Constructor, returns instance of player
     *
     * @param playerId the unique id that identifies a player
     * @param name
     * @throws IllegalArgumentException on empty or null address or name and on player = 0
     */
    public Player(String playerId, String name, int score) {
        if (playerId == null || name == null || name.isEmpty())
            throw new IllegalArgumentException();
        this.playerId = playerId;
        this.name = name;
        this.score = score;
    }


    //TODO This constructor should be removed once @Tesa fixes the merge error he created
    public Player(String playerId, String name, String address) {
        if (playerId == null || name == null || name.isEmpty() || address == null || address.isEmpty())
            throw new IllegalArgumentException();
        this.playerId = playerId;
        this.name = name;
    }

    /**
     * Setter for name. By design the player already had a name
     *
     * @param name
     * @param dbChange whether the database entry must be updated
     */
    public void setName(String name, boolean dbChange) {
        this.name = name;
        dbUpdate(dbChange);
    }

    /**
     * sets the score
     *
     * @param score    the score that is to update
     * @param dbChange whether the database entry must be updated
     */
    public void setScore(int score, boolean dbChange) {
        this.score = score;
        dbUpdate(dbChange);
    }

    /**
     * @return the unique player id
     */
    public String getPlayerId() {
        return playerId;
    }

    /**
     * @return the score of that player
     */
    public int getScore() {
        return score;
    }

    /**
     * sets the score
     *
     * @param score
     */
    public void setScore(int score) {
        this.setScore(score, false);
    }

    /**
     * @return the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Setter without db change
     *
     * @param name
     */
    public void setName(String name) {
        this.setName(name, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return playerId.equals(player.playerId) &&
                name.equals(player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, name);
    }

    /**
     * Updates player values in the firebase database
     *
     * @param dbChange
     */
    private void dbUpdate(boolean dbChange) {
        if (dbChange) {
            PlayerDatabaseProxy pdb = new PlayerDatabaseProxy();
            pdb.putPlayer(this);
        }
    }


    /**
     * @param question
     * @return the answer of the question asked
     */
    public String ask(String question) {
        String answer = "";
        //TODO: display question on  player's screen and store the response
        return answer;
    }
    //TODO: add later methods related to the game itself
}
