package sdp.moneyrun.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sdp.moneyrun.database.UserDatabaseProxy;

public class User implements Serializable {

    private String userId;
    private String name;
    private String address;
    private int numberOfPlayedGames;
    private int numberOfDiedGames;
    private int totalDistanceRun;
    private List<String> friendIdList = new ArrayList<>();
    private int maxScoreInGame;
    private String preferredColor;
    private String preferredPet;


    /**
     * For database purpose, a default constructor is needed
     */
    public User() {
    }
    public User(String userId) {
        this.userId = userId;
    }
    /**
     * Constructor, returns instance of user
     *
     * @param userId              the unique id that identifies a user
     * @param name
     * @param address
     * @param numberOfDiedGames
     * @param numberOfPlayedGames
     * @throws IllegalArgumentException on empty or null address or name and on user = 0
     */
    public User(String userId, String name, String address, int numberOfDiedGames,
                int numberOfPlayedGames, int maxScoreInGame) {
        if (userId == null || name == null || name.isEmpty() || address == null || address.isEmpty() || maxScoreInGame < 0)
            throw new IllegalArgumentException();
        this.userId = userId;
        this.name = name;
        this.address = address;
        this.numberOfDiedGames = numberOfDiedGames;
        this.numberOfPlayedGames = numberOfPlayedGames;
        this.maxScoreInGame = maxScoreInGame;
        this.totalDistanceRun = 0;
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
     * @param name
     * @param dbChange whether the database entry must be updated
     */
    public void setName(String name, boolean dbChange) {
        this.name = name;
        dbUpdate(dbChange);
    }

    /**
     * Setter for address. By design the user already had an address
     *
     * @param address
     * @param dbChange whether the database entry must be updated
     */
    public void setAddress(String address, boolean dbChange) {
        this.address = address;
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
     * @param diedGames
     * @param dbChange  whether the database entry must be updated
     */
    public void setNumberOfDiedGames(int diedGames, boolean dbChange) {
        numberOfDiedGames = diedGames;
        dbUpdate(dbChange);
    }

    /**
     * sets the number of played games
     *
     * @param playedGames
     * @param dbChange
     */
    public void setNumberOfPlayedGames(int playedGames, boolean dbChange) {
        numberOfPlayedGames = playedGames;
        dbUpdate(dbChange);
    }

    /**
     * add a friend id to the friend id list
     *
     * @param friendId the friend id to add
     * @return true if the friend id has been added, false otherwise
     */
    public boolean addFriendId(String friendId) {
        if (friendId == null) {
            throw new IllegalArgumentException("friend id should not be null");
        }

        return this.friendIdList.add(friendId);
    }

    /**
     * remove a friend id to the friend id list
     *
     * @param friendId the friend id to remove
     * @return true if the friend id has been removed, false otherwise
     */
    public boolean removeFriendId(String friendId) {
        if (friendId == null) {
            throw new IllegalArgumentException("friend should not be null");
        }

        return this.friendIdList.remove(friendId);
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
    public String getUserId() {
        return userId;
    }

    /**
     * @return the adress of the user
     */
    public String getAddress() {
        return address;
    }

    /**
     * Setter without db change
     *
     * @param address
     */
    public void setAddress(String address) {
        this.setAddress(address, false);
    }

    /**
     * @return the name of the user
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

    public List<String> getFriendIdList() {
        return new ArrayList<>(this.friendIdList);
    }

    /**
     * set the user friend list
     *
     * @param friendIdList the player id list
     */
    public void setFriendIdList(List<String> friendIdList) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId.equals(user.userId) &&
                numberOfPlayedGames == user.numberOfPlayedGames &&
                numberOfDiedGames == user.numberOfDiedGames &&
                name.equals(user.name) &&
                address.equals(user.address)
                && friendIdList.equals(user.friendIdList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, address, numberOfPlayedGames, numberOfDiedGames);
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


    /**
     * @param question
     * @return the answer of the question asked
     */
    public String ask(String question) {
        String answer = "";
        //TODO: display question on  user's screen and store the response
        return answer;
    }
    //TODO: add later methods related to the game itself
}
