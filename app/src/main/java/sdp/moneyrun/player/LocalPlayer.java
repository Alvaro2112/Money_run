package sdp.moneyrun.player;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import sdp.moneyrun.map.Coin;

/**
 * We store here all the attributes of a player that do not need no be pushed to the database and that are kept locally
 */
public class LocalPlayer implements Serializable {

    /**
     * Coins that this player lost (ie. Answered incorrectly to the riddle associated with this coin). Other players can still see these coins
     */
    @NonNull
    private final ArrayList<Coin> lostCoins;

    /**
     * Coins that this player collected (ie. Answered correctly to the riddle associated with this coin), it should be synced with the database and other players will
     * not be able to see these coins
     */
    @NonNull
    private final ArrayList<Coin> collectedCoins;

    /**
     * The coins that the player sees locally (ie. what this player can collect, others might be able to see more or less coins)
     */
    @Nullable
    private ArrayList<Coin> locallyAvailableCoins;

    private int score;

    public LocalPlayer() {
        this.lostCoins = new ArrayList<>();
        this.locallyAvailableCoins = new ArrayList<>();
        this.collectedCoins = new ArrayList<>();
        score = 0;
    }

    @Nullable
    public ArrayList<Coin> getLocallyAvailableCoins() {
        return locallyAvailableCoins;
    }

    public void setLocallyAvailableCoins(@Nullable ArrayList<Coin> locallyAvailableCoins) {
        this.locallyAvailableCoins = locallyAvailableCoins;
    }

    @NonNull
    public ArrayList<Coin> getLostCoins() {
        return lostCoins;
    }

    @NonNull
    public ArrayList<Coin> getCollectedCoins() {
        return collectedCoins;
    }

    public int getScore() {
        return score;
    }

    /**
     * Adds a coin that was lost because of an incorrectly answered riddle
     *
     * @param coin The coin to be added
     */
    public void addLostCoin(@Nullable Coin coin) {
        if (coin == null) {
            throw new IllegalArgumentException("The coin to be added cannot be null");
        }

        lostCoins.add(coin);
    }

    /**
     * Adds a coin that was collected because of an correctly answered riddle
     *
     * @param coin The coin to be added
     */
    public void addCollectedCoin(@Nullable Coin coin) {
        if (coin == null) {
            throw new IllegalArgumentException("The coin to be added cannot be null");
        }

        collectedCoins.add(coin);
    }

    public void addLocallyAvailableCoin(@Nullable Coin coin) {
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
    public void updateLostCoins(@Nullable ArrayList<Coin> availableCoins) {
        if (availableCoins == null) {
            throw new IllegalArgumentException("The availableCoins cannot be null");
        }
        if (availableCoins.contains(null)) {
            throw new IllegalArgumentException("The availableCoins cannot contain a null coin");
        }

        ArrayList<Coin> toRemove = new ArrayList<>(lostCoins);
        toRemove.removeAll(availableCoins);
        this.lostCoins.removeAll(toRemove);
    }

    /**
     * This function will remove the coin from the corresponding lists depending on whether is was picked up or not and
     * will also update the score of the player if necessary.
     *
     * @param coin     The coin to be removed
     * @param pickedUp whether the coins was picked up (ie. the player answered correctly to the riddle) or not
     */
    public void updateCoins(@Nullable Coin coin, boolean pickedUp) {
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
    public void syncAvailableCoinsFromDb(@Nullable ArrayList<Coin> availableCoins) {
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
    @NonNull
    public ArrayList<Coin> toSendToDb() {
        Set<Coin> set = new HashSet<>();

        set.addAll(locallyAvailableCoins);
        set.addAll(lostCoins);

        return new ArrayList<>(set);
    }

}
