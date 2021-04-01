package sdp.moneyrun;

import java.util.Objects;

public class Player {

    private  int playerId;
    private String name;
    private String address;
    private int numberOfPlayedGames;
    private int numberOfDiedGames;
    private int totalDistanceRun;
    private int maxScoreInGame;
    private String preferredColor;
    private String preferredPet;
    private int score;

    /*
    For database purpose, a default constructor is needed
     */
    public Player(){}


    public Player(int playerId, String name, String address, int numberOfDiedGames,
                  int numberOfPlayedGames){
        this.playerId = playerId;
        this.setName(name);
        this.setAddress(address);
        this.numberOfDiedGames = numberOfDiedGames;
        this.numberOfPlayedGames = numberOfPlayedGames;
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


    public int getScore(){
        if (name == null)
            throw new IllegalStateException();

        return score;
    }

    public void setScore(int score){
        this.score = score;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return playerId == player.playerId &&
                numberOfPlayedGames == player.numberOfPlayedGames &&
                numberOfDiedGames == player.numberOfDiedGames &&
                name.equals(player.name) &&
                address.equals(player.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, name, address, numberOfPlayedGames, numberOfDiedGames);
    }


    public String ask(String question){
        String answer = "";
        //TODO: display question on  player's screen and store the response
        return answer;
    }
    //TODO: add later methods related to the game itself
}
