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

    private final ArrayList<Coin> lostCoins;
    private final ArrayList<Coin> collectedCoins;
    private ArrayList<Coin> locallyAvailableCoins;
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

    public ArrayList<Coin> getCollectedCoins() {
        return collectedCoins;
    }

    public int getScore() {
        return score;
    }

    public void addLostCoin(Coin coin) {
        if (coin == null) {
            throw new IllegalArgumentException("The coin to be added cannot be null");
        }

        lostCoins.add(coin);
    }

    public void addCollectedCoin(Coin coin) {
        if (coin == null) {
            throw new IllegalArgumentException("The coin to be added cannot be null");
        }

        collectedCoins.add(coin);
    }

    public void addLocallyAvailableCoin(Coin coin) {
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
     * This function will remove the coin from the corresponding lists depending on wheter is was picked up or not and
     * will also update the score of the player of necessary.
     *
     * @param coin     The coin to be removed
     * @param pickedUp whether the coins was picked up (ie. the player answered correctly to the riddle) or not
     */
    public void updateCoins(Coin coin, boolean pickedUp) {
        if (coin == null) {
            throw new IllegalArgumentException("cannot remove a null coin");
        }

        if (pickedUp) {
            addCollectedCoin(coin);
            score += coin.getValue();
        } else {
            addLostCoin(coin);
        }

        locallyAvailableCoins.remove(coin);

    }

    /**
     * This function will update all variables of the LocalPlayer based on the newly received available coins.
     *
     * @param availableCoins Updated list of available coins received from database
     */
    public void syncAvailableCoinsFromDb(ArrayList<Coin> availableCoins) {
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

    /**
     * @return This function will return the new list of available coins that needs to be shared with everyone (ie. send to DB)
     */
    public ArrayList<Coin> toSendToDb() {
        Set<Coin> set = new HashSet<Coin>();

        set.addAll(locallyAvailableCoins);
        set.addAll(lostCoins);

        return new ArrayList<Coin>(set);
    }

}
