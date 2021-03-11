package sdp.moneyrun;

public class Player {
    private int playerId;
    private String name;
    private String address;
    private int numberOfPlayedGames;
    private int numberOfDiedGames;

    public Player(int playerId) {
        this.playerId = playerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void updatePlayedGames() {
        numberOfPlayedGames++;
    }

    public void updateDiedGames() {
        numberOfDiedGames++;
    }

    public int getNumberOfDiedGames() {
        return numberOfDiedGames;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getAddress() {
        if (address == null)
            throw new IllegalStateException();
        return address;
    }

    public String getName() {
        if (name == null)
            throw new IllegalStateException();
        return name;
    }

    public int getNumberOfPlayedGames() {
        return numberOfPlayedGames;
    }
    //TODO: add later methods related to the game itself
}
