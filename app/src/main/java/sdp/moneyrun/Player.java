package sdp.moneyrun;

public class Player {
    private final int playerId;
    private String name;
    private String address;
    private int numberOfPlayedGames;
    private int numberOfDiedGames;

    public Player(int playerId) {
        this.playerId = playerId;
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

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        if (name == null)
            throw new IllegalStateException();
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfPlayedGames() {
        return numberOfPlayedGames;
    }

    public String ask(String question){
        String answer = "";
        //TODO: display question on  player's screen and store the response
        return answer;
    }
    //TODO: add later methods related to the game itself
}
