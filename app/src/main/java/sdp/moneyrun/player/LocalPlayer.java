package sdp.moneyrun.player;

import java.io.Serializable;
import java.util.ArrayList;

import sdp.moneyrun.map.Coin;

/**
 * We store here all the attributes of a player that do not need no be pushed to the database
 */
public class LocalPlayer  implements Serializable {

    private ArrayList<Coin> lostCoins;
    private ArrayList<Coin> locallyAvailableCoins;

    public LocalPlayer(){
        this.lostCoins = new ArrayList<Coin>();
    }

    public ArrayList<Coin> getLocallyAvailableCoins() {
        return locallyAvailableCoins;
    }

    public ArrayList<Coin> getLostCoins() {
        return lostCoins;
    }

    public void addLostCoin(Coin coin){
        if(coin == null){
            throw new IllegalArgumentException("The coin to be added cannot be null");
        }

        lostCoins.add(coin);
    }

    public void updateLostCoins(ArrayList<Coin> availableCoins){
        if(availableCoins == null){
            throw new IllegalArgumentException("The availableCoins cannot be null");
        }
        if(availableCoins.contains(null)){
            throw new IllegalArgumentException("The availableCoins cannot contain a null coin");
        }

        ArrayList<Coin> toRemove = lostCoins;
        lostCoins.removeAll(availableCoins);
        this.lostCoins.removeAll(toRemove);
    }

    public void updateLocallyAvailableCoins(ArrayList<Coin> availableCoins, boolean locally, Coin toRemove){
        if(availableCoins == null){
            throw new IllegalArgumentException("The availableCoins cannot be null");
        }
        if(availableCoins.contains(null)){
            throw new IllegalArgumentException("The availableCoins cannot contain a null coin");
        }

        if(!locally){
            updateLostCoins(availableCoins);
            this.locallyAvailableCoins = availableCoins;
            this.locallyAvailableCoins.removeAll(lostCoins);
        }else{
            addLostCoin(toRemove);
            this.locallyAvailableCoins.remove(toRemove);
        }

    }

}
