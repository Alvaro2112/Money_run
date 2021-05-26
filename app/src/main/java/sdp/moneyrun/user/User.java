package sdp.moneyrun.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sdp.moneyrun.database.UserDatabaseProxy;

/**
 * This class represents a user (ie. A person), not to be confused with a Player. A user is the entity that uses the app and always exist, a Player only exists during a Game and is created by a User.
 */
public class User implements Serializable {

    @Nullable
    private String userId;
    @Nullable
    private String name;
    private int numberOfPlayedGames;
    private int numberOfDiedGames;
    @NonNull
    private List<String> friendIdList = new ArrayList<>();
    private int maxScoreInGame;


    /**
     * For database purpose, a default constructor is needed
     */
    public User() {
    }

    public User(@Nullable String userId) {
        if (userId == null) {
            throw new NullPointerException("UserID is null");
        }
        this.userId = userId;
    }

    /**
     * Constructor, returns instance of user
     *
     * @param userId              the unique id that identifies a user
     * @param name the name of the user
     * @param numberOfDiedGames the number of games a user lost
     * @param numberOfPlayedGames the number of games a user played
     * @param maxScoreInGame  the highest score this user achieved in any game
     * @throws IllegalArgumentException on empty or null name and on user = 0
     */
    public User(@Nullable String userId, @Nullable String name, int numberOfDiedGames,
                int numberOfPlayedGames, int maxScoreInGame) {
        if (userId == null)
            throw new IllegalArgumentException("The user ID cannot be null");

        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("The name of the user cannot be null");

        if (maxScoreInGame < 0)
            throw new IllegalArgumentException("The max score of a user must be positive");

        this.userId = userId;
        this.name = name;
        this.numberOfDiedGames = numberOfDiedGames;
        this.numberOfPlayedGames = numberOfPlayedGames;
        this.maxScoreInGame = maxScoreInGame;
    }

    public int getMaxScoreInGame() {
        return maxScoreInGame;
    }

    public void setMaxScoreInGame(int maxScoreInGame) {
        setMaxScoreInGame(maxScoreInGame, false);
    }

    public void setMaxScoreInGame(int maxScoreInGame, boolean dbChange) {
        this.maxScoreInGame = maxScoreInGame;
        dbUpdate(dbChange);

    }

    /**
     * Setter for name. By design the user already had a name
     *
     * @param name     The new name of the user
     * @param dbChange whether the database entry must be updated
     */
    public void setName(String name, boolean dbChange) {
        this.name = name;
        dbUpdate(dbChange);
    }


    /**
     * Increments the number of played games
     */
    public void updatePlayedGames() {
        this.updatePlayedGames(false);
    }

    /**
     * Increments the number of played games
     *
     * @param dbChange whether the database entry must be updated
     */
    public void updatePlayedGames(boolean dbChange) {
        numberOfPlayedGames++;
        dbUpdate(dbChange);
    }

    /**
     * Increments the number of died games
     */
    public void updateDiedGames() {
        this.updateDiedGames(false);
    }

    /**
     * Increments the number of died games
     *
     * @param dbChange whether the database entry must be updated
     */
    public void updateDiedGames(boolean dbChange) {
        numberOfDiedGames++;
        dbUpdate(dbChange);
    }

    /**
     * sets the number of died games
     *
     * @param diedGames The new number of games this user lost
     * @param dbChange  whether the database entry must be updated
     */
    public void setNumberOfDiedGames(int diedGames, boolean dbChange) {
        numberOfDiedGames = diedGames;
        dbUpdate(dbChange);
    }

    /**
     * sets the number of played games
     *
     * @param playedGames The new number of games this user player
     * @param dbChange    Whether to update the database or not
     */
    public void setNumberOfPlayedGames(int playedGames, boolean dbChange) {
        numberOfPlayedGames = playedGames;
        dbUpdate(dbChange);
    }

    /**
     * add a friend id to the friend id list
     *
     * @param friendId the friend id to add
     */
    public void addFriendId(@Nullable String friendId) {
        if (friendId == null) {
            throw new IllegalArgumentException("friend id should not be null");
        }

        if (!this.friendIdList.contains(friendId)) {
            this.friendIdList.add(friendId);
        }
    }

    /**
     * remove a friend id to the friend id list
     *
     * @param friendId the friend id to remove
     */
    public void removeFriendId(@Nullable String friendId) {
        if (friendId == null) {
            throw new IllegalArgumentException("friend should not be null");
        }

        this.friendIdList.remove(friendId);
    }

    /**
     * @return number of games in which the user died
     */
    public int getNumberOfDiedGames() {
        return numberOfDiedGames;
    }

    /**
     * sets the number of died games
     *
     * @param diedGames
     */
    public void setNumberOfDiedGames(int diedGames) {
        this.setNumberOfDiedGames(diedGames, false);
    }

    /**
     * @return the unique user id
     */
    @Nullable
    public String getUserId() {
        return userId;
    }

    public void setUserId(@Nullable String userId) {
        if (userId == null) {
            throw new NullPointerException();
        }
        this.userId = userId;
    }

    /**
     * @return the name of the user
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

    /**
     * @return the number of games the user played
     */
    public int getNumberOfPlayedGames() {
        return numberOfPlayedGames;
    }

    /**
     * sets the number of played games
     *
     * @param playedGames
     */
    public void setNumberOfPlayedGames(int playedGames) {
        this.setNumberOfPlayedGames(playedGames, false);
    }

    @NonNull
    public List<String> getFriendIdList() {
        return new ArrayList<>(this.friendIdList);
    }


    /**
     * set the user friend list
     *
     * @param friendIdList the player id list
     */
    public void setFriendIdList(@Nullable List<String> friendIdList) {
        if (friendIdList == null) {
            throw new IllegalArgumentException("friend id list should not be null");
        }
        for (String friend : friendIdList) {
            if (friend == null) {
                throw new IllegalArgumentException("friend id in friend id list should not be null");
            }
        }

        this.friendIdList = new ArrayList<>(friendIdList);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;

        return sameAttributes(user);
    }

    /**
     * @param user the other user
     * @return true if user has same attributes as this
     */
    private boolean sameAttributes(@NonNull User user) {
        return Objects.equals(userId, user.userId) &&
                numberOfPlayedGames == user.numberOfPlayedGames &&
                numberOfDiedGames == user.numberOfDiedGames &&
                Objects.equals(name, user.name) &&
                Objects.equals(friendIdList, user.friendIdList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, numberOfPlayedGames, numberOfDiedGames);
    }

    /**
     * Updates user values in the firebase database
     *
     * @param dbChange
     */
    private void dbUpdate(boolean dbChange) {
        if (dbChange) {
            UserDatabaseProxy pdb = new UserDatabaseProxy();
            pdb.putUser(this);
        }
    }

}
