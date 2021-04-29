package sdp.moneyrun.player;

import java.io.Serializable;
import java.util.ArrayList;

import sdp.moneyrun.map.Coin;

/**
 * We store here all the attributes of a player that do not need no be pushed to the database
 */
public class LocalPlayer implements Serializable {

    private final ArrayList<Coin> lostCoins;
    private ArrayList<Coin> locallyAvailableCoins;

    public LocalPlayer() {
        this.lostCoins = new ArrayList<Coin>();
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

    public void addLostCoin(Coin coin) {
        if (coin == null) {
            throw new IllegalArgumentException("The coin to be added cannot be null");
        }

        lostCoins.add(coin);
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
     * @param availableCoins The list of coins that are still available to EVERYONE, this argument can be null when locally is set to true
     * @param locally Whether we are only removing a Coin locally or updating everything based on the list
     *                of available coins from the dataBase.
     * @param toRemove The coin to remove if locally set to true, this argument can be null when locally is set to false
     */
    public void updateLocallyAvailableCoins(ArrayList<Coin> availableCoins, boolean locally, Coin toRemove) {

        if (!locally) {

            if (availableCoins == null) {
                throw new IllegalArgumentException("The availableCoins cannot be null");
            }
            if (availableCoins.contains(null)) {
                throw new IllegalArgumentException("The availableCoins cannot contain a null coin");
            }

            updateLostCoins(availableCoins);
            this.locallyAvailableCoins = availableCoins;
            this.locallyAvailableCoins.removeAll(lostCoins);
        } else {
            if (toRemove == null) {
                throw new IllegalArgumentException("cannot remove a null coin");
            }
            addLostCoin(toRemove);
            this.locallyAvailableCoins.remove(toRemove);
        }

    }

}
