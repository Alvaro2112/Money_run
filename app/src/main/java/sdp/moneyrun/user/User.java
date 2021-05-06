package sdp.moneyrun.user;

import java.io.Serializable;
import java.util.Objects;
import java.io.Serializable;
import java.util.Objects;
import sdp.moneyrun.database.UserDatabaseProxy;

public class User implements Serializable {

        private int userId;
        private String name;
        private String address;
        private int numberOfPlayedGames;
        private int numberOfDiedGames;
        private int totalDistanceRun;

    public int getMaxScoreInGame() {
        return maxScoreInGame;
    }

    public void setMaxScoreInGame(int maxScoreInGame,  boolean dbChange) {
        this.maxScoreInGame = maxScoreInGame;
        dbUpdate(dbChange);

    }

    public void setMaxScoreInGame(int maxScoreInGame) {
        setMaxScoreInGame(maxScoreInGame, false);
    }



    private int maxScoreInGame;
        private String preferredColor;
        private String preferredPet;

        /**
         * For database purpose, a default constructor is needed
         */
        public User(){}

        public User(int userId){
            this.userId = userId;
        }


        /**
         * Constructor, returns instance of user
         * @param userId the unique id that identifies a user
         * @param name
         * @param address
         * @param numberOfDiedGames
         * @param numberOfPlayedGames
         * @throws IllegalArgumentException on empty or null address or name and on user = 0
         */
        public User(int userId, String name, String address, int numberOfDiedGames,
                      int numberOfPlayedGames, int maxScoreInGame){
            if (userId == 0 || name == null || name.isEmpty() || address == null ||address.isEmpty() || maxScoreInGame < 0)
                throw new IllegalArgumentException();
            this.userId = userId;
            this.name = name;
            this.address = address;
            this.numberOfDiedGames = numberOfDiedGames;
            this.numberOfPlayedGames = numberOfPlayedGames;
            this.maxScoreInGame = maxScoreInGame;
            this.totalDistanceRun = 0;
        }


        /**
         * Setter for name. By design the user already had a name
         * @param name
         * @param dbChange whether the database entry must be updated
         */
        public void setName(String name, boolean dbChange) {
            this.name = name;
            dbUpdate(dbChange);
        }

        /**
         * Setter without db change
         * @param name
         */
        public void setName(String name){
            this.setName(name, false);
        }

        /**
         *Setter for address. By design the user already had an address
         * @param address
         * @param dbChange whether the database entry must be updated
         */
        public void setAddress(String address, boolean dbChange) {
            this.address = address;
            dbUpdate(dbChange);
        }

        /**
         * Setter without db change
         * @param address
         */
        public void setAddress(String address){
            this.setAddress(address, false);
        }

        /**
         * Increments the number of played games
         */
        public void updatePlayedGames(){
            this.updatePlayedGames(false);
        }

        /**
         * Increments the number of played games
         * @param dbChange whether the database entry must be updated
         */
        public void updatePlayedGames(boolean dbChange) {
            numberOfPlayedGames++;
            dbUpdate(dbChange);
        }

        /**
         * Increments the number of died games
         */
        public void updateDiedGames(){
            this.updateDiedGames(false);
        }

        /**
         * Increments the number of died games
         * @param dbChange whether the database entry must be updated
         */
        public void updateDiedGames(boolean dbChange) {
            numberOfDiedGames++;
            dbUpdate(dbChange);
        }

        /**
         * sets the number of died games
         * @param diedGames
         * @param dbChange whether the database entry must be updated
         */
        public void setNumberOfDiedGames (int diedGames, boolean dbChange){
            numberOfDiedGames = diedGames;
            dbUpdate(dbChange);
        }

        /**
         * sets the number of died games
         * @param diedGames
         */
        public void setNumberOfDiedGames (int diedGames){
            this.setNumberOfDiedGames(diedGames, false);
        }

        /**
         * sets the number of played games
         * @param playedGames
         * @param dbChange
         */
        public void setNumberOfPlayedGames(int playedGames, boolean dbChange){
            numberOfPlayedGames = playedGames;
            dbUpdate(dbChange);
        }

        /**
         * sets the number of played games
         * @param playedGames
         */
        public void setNumberOfPlayedGames(int playedGames){
            this.setNumberOfPlayedGames(playedGames, false);
        }


        /**
         *
         * @return number of games in which the user died
         */
        public int getNumberOfDiedGames() {
            return numberOfDiedGames;
        }

        /**
         *
         * @return the unique user id
         */
        public int getUserId() {
            return userId;
        }

        /**
         *
         * @return the adress of the user
         */
        public String getAddress() {
            return address;
        }

        /**
         *
         * @return the name of the user
         */
        public String getName() {
            return name;
        }

        /**
         *
         * @return the number of games the user played
         */
        public int getNumberOfPlayedGames() {
            return numberOfPlayedGames;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            User user = (User) o;
            return userId == user.userId &&
                    numberOfPlayedGames == user.numberOfPlayedGames &&
                    numberOfDiedGames == user.numberOfDiedGames &&
                    name.equals(user.name) &&
                    address.equals(user.address);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, name, address, numberOfPlayedGames, numberOfDiedGames);
        }

        /**
         * Updates user values in the firebase database
         * @param dbChange
         */
        private void dbUpdate(boolean dbChange){
            if(dbChange){
                UserDatabaseProxy pdb = new UserDatabaseProxy();
                pdb.putUser(this);
            }
        }


        /**
         *
         * @param question
         * @return the answer of the question asked
         */
        public String ask(String question){
            String answer = "";
            //TODO: display question on  user's screen and store the response
            return answer;
        }
        //TODO: add later methods related to the game itself
}
