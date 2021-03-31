package sdp.moneyrun;

/**
 * This class implements a representation of a game, a class containing informations of
 * a game that is not automatically updated by the database.
 *
 * @author Arnaud Poletto
 */
public class GameRepresentation {
    private final String gameId;
    private final String name;
    private final int playerCount;
    private final int maxPlayerCount;

    /**
     *
     * @param gameId            the id of the game
     * @param name              the name of the game
     * @param playerCount       the current number of players in the game
     * @param maxPlayerCount    the maximum number of players in the game
     */
    public GameRepresentation(String gameId, String name, int playerCount, int maxPlayerCount){
        if(gameId == null){
            throw new IllegalArgumentException("gameId should not be null.");
        }
        if(name == null){
            throw new IllegalArgumentException("name should not be null.");
        }

        this.gameId = gameId;
        this.name = name;
        this.playerCount = playerCount;
        this.maxPlayerCount = maxPlayerCount;
    }

    /**
     * @return the id of the game
     */
    public String getGameId(){
        return gameId;
    }

    /**
     * @return the name of the game
     */
    public String getName(){
        return name;
    }

    /**
     * @return the current number of players in the game
     */
    public int getPlayerCount(){
        return playerCount;
    }

    /**
     * @return the maximum number of players in the game
     */
    public int getMaxPlayerCount(){
        return maxPlayerCount;
    }
}
