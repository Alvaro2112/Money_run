package sdp.moneyrun.player;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

import sdp.moneyrun.database.PlayerDatabaseProxy;

public class Player implements Serializable {

    @Nullable
    private String playerId;
    @Nullable
    private String name;
    private int score;

    /**
     * For database purpose, a default constructor is needed
     */
    public Player() {
    }

    public Player(@Nullable String playerId) {
        this.playerId = playerId;
    }


    /**
     * Constructor, returns instance of player
     *
     * @param playerId the unique id that identifies a player
     * @param name
     * @throws IllegalArgumentException on empty or null name and on player = 0
     */
    public Player(@Nullable String playerId, @Nullable String name, int score) {
        if (playerId == null || name == null || name.isEmpty())
            throw new IllegalArgumentException();
        this.playerId = playerId;
        this.name = name;
        this.score = score;
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
    @Nullable
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
    @Nullable
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
    public boolean equals(@Nullable Object o) {
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

}
