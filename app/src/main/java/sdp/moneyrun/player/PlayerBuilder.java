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
    @NonNull
    public PlayerBuilder setPlayerId(String playerId) {
        this.playerId = playerId;

        return this;
    }

    /**
     * set the name of the build player
     * @param name
     * @return instance to chain calls
     */
    @NonNull
    public PlayerBuilder setName(@Nullable String name) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException();
        this.name = name;

        return this;
    }

    /**
     * set the score of the build player
     * @param score
     * @return instance to chain calls
     */
    @NonNull
    public PlayerBuilder setScore(int score) {
        this.score = score;

        return this;
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
