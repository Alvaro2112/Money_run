package sdp.moneyrun;

public class PlayerBuilder {
    private  int playerId;
    private String name;
    private String address;
    private int numberOfPlayedGames;
    private int numberOfDiedGames;

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException();
        this.name = name;
    }

    public void setAddress(String address) {
        if (address == null || address.isEmpty())
            throw new IllegalArgumentException();
        this.address = address;
    }

    public void setNumberOfPlayedGames(int numberOfPlayedGames) {
        this.numberOfPlayedGames = numberOfPlayedGames;
    }

    public void setNumberOfDiedGames(int numberOfDiedGames) {
        this.numberOfDiedGames = numberOfDiedGames;
    }



    public PlayerBuilder(){}

    public Player build(){
        if(playerId == 0 || name == null || address == null)
            throw new IllegalStateException();
        return new Player(playerId,name, address, numberOfDiedGames, numberOfPlayedGames);
    }


}
