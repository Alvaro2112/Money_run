package sdp.moneyrun.player;

import java.io.Serializable;
import java.util.ArrayList;

import sdp.moneyrun.map.Coin;

/**
 * We store here all the attributes of a player that do not need no be pushed to the database
 */
public class LocalPlayer  implements Serializable {

    private ArrayList<Coin> lostCoins;

    public void LocalPlayer(){
     this.lostCoins = new ArrayList<Coin>();
    }

    public void addLostCoin(Coin coin){
        lostCoins.add(coin);
    }




}
