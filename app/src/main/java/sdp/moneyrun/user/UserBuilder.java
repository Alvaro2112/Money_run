package sdp.moneyrun.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UserBuilder {
    private String userId;
    @Nullable
    private String name;
    @Nullable
    private int numberOfPlayedGames;
    private int numberOfDiedGames;
    private int score;

    /**
     * Empty constructor
     */
    public UserBuilder() {
    }

    /**
     * @param userId the unique user Id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @param name
     */
    public void setName(@Nullable String name) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException();
        this.name = name;
    }

    /**
     * @param numberOfPlayedGames
     */
    public void setNumberOfPlayedGames(int numberOfPlayedGames) {
        this.numberOfPlayedGames = numberOfPlayedGames;
    }

    public void setNumberOfDiedGames(int numberOfDiedGames) {
        this.numberOfDiedGames = numberOfDiedGames;
    }

    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Builds current instance. name cannot be null or empty and userId cannot be 0
     *
     * @return the user built with the attributes set
     * @throws IllegalStateException if the name is null or empty, if the userId is null
     */
    @NonNull
    public User build() {
        if (userId == null || name == null)
            throw new IllegalStateException();
        return new User(userId, name, numberOfDiedGames, numberOfPlayedGames, score);
    }


}
