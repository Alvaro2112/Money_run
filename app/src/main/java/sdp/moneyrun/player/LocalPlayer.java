package sdp.moneyrun.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import sdp.moneyrun.map.Coin;

/**
 * We store here all the attributes of a player that do not need no be pushed to the database
 */
public class LocalPlayer implements Serializable {

    private ArrayList<Coin> lostCoins;
    private ArrayList<Coin> locallyAvailableCoins;
    private ArrayList<Coin> collectedCoins;
    private int score;

    public LocalPlayer() {

        this.lostCoins = new ArrayList<Coin>();
        this.locallyAvailableCoins = new ArrayList<Coin>();
        this.collectedCoins = new ArrayList<Coin>();
        score = 0;
    }

    public ArrayList<Coin> getLocallyAvailableCoins() {
        return locallyAvailableCoins;
    }

    public void setLocallyAvailableCoins(ArrayList<Coin> locallyAvailableCoins) {
        this.locallyAvailableCoins = locallyAvailableCoins;
    }

    public ArrayList<Coin> getLostCoins() {
        return lostCoins;
    }

    public ArrayList<Coin> getCollectedCoins(){
        return collectedCoins;
    }

    public int getScore(){
        return score;
    }

    public void addLostCoin(Coin coin) {
        if (coin == null) {
            throw new IllegalArgumentException("The coin to be added cannot be null");
        }

        lostCoins.add(coin);
    }

    public void addCollectedCoin(Coin coin){
        if (coin == null) {
            throw new IllegalArgumentException("The coin to be added cannot be null");
        }

        collectedCoins.add(coin);
    }

    public void addlocallyAvailableCoinsCoin(Coin coin){
        if (coin == null) {
            throw new IllegalArgumentException("The coin to be added cannot be null");
        }

        locallyAvailableCoins.add(coin);
    }


    /**
     * This function will take the coins that are still available in the database and will update the lostCoins list, this is so we
     * don't keep coins in the lostCoins list that were removed for everyone after somebody else picked it up.
     *
     * @param availableCoins Newly made aware availableCoins that were just received from the database
     */
    public void updateLostCoins(ArrayList<Coin> availableCoins) {
        if (availableCoins == null) {
            throw new IllegalArgumentException("The availableCoins cannot be null");
        }
        if (availableCoins.contains(null)) {
            throw new IllegalArgumentException("The availableCoins cannot contain a null coin");
        }

        ArrayList<Coin> toRemove = new ArrayList<Coin>(lostCoins);
        toRemove.removeAll(availableCoins);
        this.lostCoins.removeAll(toRemove);
    }

    /**
     * This is the function that will do all the work, if the locally parameter is set to true it will only remove the given coin from
     * the locallyAvailableCoins and add it to the lostCoins. If locally is set to false, it will update both lists using the newly availableCoins
     * list from the database.
     *
     * @param coin The coin to remove if locally set to true
     */
    public void updateCoins(Coin coin, boolean pickedUp) {
            if (coin == null) {
                throw new IllegalArgumentException("cannot remove a null coin");
            }

            if(pickedUp){
                addCollectedCoin(coin);
                score += coin.getValue();
            }else{
                addLostCoin(coin);
            }

        locallyAvailableCoins.remove(coin);


    }

    public void syncAvailableCoinsFromDb(ArrayList<Coin> availableCoins){
        if (availableCoins == null) {
            throw new IllegalArgumentException("The availableCoins cannot be null");
        }
        if (availableCoins.contains(null)) {
            throw new IllegalArgumentException("The availableCoins cannot contain a null coin");
        }

        updateLostCoins(availableCoins);
        this.locallyAvailableCoins = availableCoins;
        this.locallyAvailableCoins.removeAll(lostCoins);
    }

    public ArrayList<Coin> toSendToDb(){
        Set<Coin> set = new HashSet<Coin>();

        set.addAll(locallyAvailableCoins);
        set.addAll(lostCoins);

        return new ArrayList<Coin>(set);
    }

}
