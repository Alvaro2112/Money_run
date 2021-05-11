package sdp.moneyrun.player;

public class PlayerBuilder {
    private  String playerId;
    private String name;
    private String address;
    private int numberOfPlayedGames;
    private int numberOfDiedGames;
    private int  score;

    /**
     *
     * @param playerId the unique player Id
     */
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException();
        this.name = name;
    }

    /**
     *
     * @param address
     */
    public void setAddress(String address) {
        if (address == null || address.isEmpty())
            throw new IllegalArgumentException();
        this.address = address;
    }

    /**
     *
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
     * Empty constructor
     */
    public PlayerBuilder(){}

    /**
     * Builds current instance. Address and name cannot be null or empty and playerId cannot be 0
     * @return the player built with the attributes set
     * @throws IllegalStateException if the adress or name is null or empty, if the playerId is null
     */
    public Player build(){
        if(playerId == null || name == null)
            throw new IllegalStateException();
        return new Player(playerId, name, score);
    }


}
