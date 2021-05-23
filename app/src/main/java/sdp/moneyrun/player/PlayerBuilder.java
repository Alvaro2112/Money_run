package sdp.moneyrun.player;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PlayerBuilder {
    private String playerId;
    @Nullable
    private String name;
    private int score;

    /**
     * Empty constructor
     */
    public PlayerBuilder() {
    }

    /**
     * @param playerId the unique player Id
     */
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    /**
     * @param name
     */
    public void setName(@Nullable String name) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException();
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Builds current instance. name cannot be null or empty and playerId cannot be 0
     *
     * @return the player built with the attributes set
     * @throws IllegalStateException if the name is null or empty, if the playerId is null
     */
    @NonNull
    public Player build() {
        if (playerId == null || name == null)
            throw new IllegalStateException();
        return new Player(playerId, name, score);
    }


}
