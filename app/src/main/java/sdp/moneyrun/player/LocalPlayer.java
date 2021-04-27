package sdp.moneyrun.player;

import java.util.ArrayList;

import sdp.moneyrun.map.Coin;

/**
 * We store here all the attributes of a player that do not need no be pushed to the database
 */
public class LocalPlayer {

    private ArrayList<Coin> lostCoins;

    public void LocalPlayer(){
     this.lostCoins = new ArrayList<Coin>();
    }




}
