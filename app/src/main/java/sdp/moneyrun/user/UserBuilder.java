package sdp.moneyrun.user;

public class UserBuilder {
    private  String userId;
    private String name;
    private String address;
    private int numberOfPlayedGames;
    private int numberOfDiedGames;
    private int  score;

    /**
     *
     * @param userId the unique user Id
     */
    public void setUserId(String userId) {
        this.userId = userId;
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
    public UserBuilder(){}

    /**
     * Builds current instance. Address and name cannot be null or empty and userId cannot be 0
     * @return the user built with the attributes set
     * @throws IllegalStateException if the adress or name is null or empty, if the userId is null
     */
    public User build(){
        if(userId == null || name == null || address == null)
            throw new IllegalStateException();
        return new User(userId, name, address, numberOfDiedGames, numberOfPlayedGames, score);
    }


}
