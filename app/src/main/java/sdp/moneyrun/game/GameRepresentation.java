package sdp.moneyrun.game;

import sdp.moneyrun.map.LocationRepresentation;

/**
 * This class implements a representation of a game, a class containing information of
 * a game that is not automatically updated by the database.
 */
public class GameRepresentation {
    private final String gameId;
    private final String name;
    private final int maxPlayerCount;
    private final LocationRepresentation startLocation;
    private int playerCount;

    /**
     * @param gameId         the id of the game
     * @param name           the name of the game
     * @param playerCount    the current number of players in the game
     * @param maxPlayerCount the maximum number of players in the game
     */
    public GameRepresentation(String gameId, String name, int playerCount, int maxPlayerCount, LocationRepresentation startLocation) {
        if (gameId == null) {
            throw new IllegalArgumentException("gameId should not be null.");
        }
        if (name == null) {
            throw new IllegalArgumentException("name should not be null.");
        }
        if (startLocation == null) {
            throw new IllegalArgumentException("location should not be null.");
        }

        this.gameId = gameId;
        this.name = name;
        this.playerCount = playerCount;
        this.maxPlayerCount = maxPlayerCount;
        this.startLocation = startLocation;
    }

    /**
     * @return the id of the game
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * @return the name of the game
     */
    public String getName() {
        return name;
    }

    /**
     * @return the current number of players in the game
     */
    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int t) {
        if (t < 1) {
            throw new IllegalArgumentException("Tried to set playerCount to " + t + " but it may not be less than 1");
        }
        playerCount = t;
    }

    /**
     * @return the maximum number of players in the game
     */
    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    /**
     * @return the start location of the game
     */
    public LocationRepresentation getStartLocation() {
        return startLocation;
    }
}
