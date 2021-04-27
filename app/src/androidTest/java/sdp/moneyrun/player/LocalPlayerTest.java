package sdp.moneyrun.player;

import androidx.test.espresso.NoMatchingViewException;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.map.Coin;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class LocalPlayerTest {

    @Test
    public void addLostCoinWorks(){

        LocalPlayer localPlayer = new LocalPlayer();
        Coin a = new Coin(1, 1, 1);
        Coin b = new Coin(2, 1, 1);
        ArrayList<Coin> c = new ArrayList<Coin>();
        c.add(a);
        c.add(b);
        localPlayer.addLostCoin(a);
        localPlayer.addLostCoin(b);

        assertThat(localPlayer.getLostCoins(), is(c));

    }

    @Test(expected = IllegalArgumentException.class)
    public void addLostCoinFailsOnNullCoin(){
        LocalPlayer localPlayer = new LocalPlayer();
        localPlayer.addLostCoin(null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void updateLostCoinsWorksOnWrongArgument1(){
        LocalPlayer localPlayer = new LocalPlayer();
        localPlayer.updateLostCoins(null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void updateLostCoinsWorksOnWrongArgument2(){
        LocalPlayer localPlayer = new LocalPlayer();
        ArrayList<Coin> c = new ArrayList<Coin>();
        c.add(null);
        localPlayer.updateLostCoins(c);

    }

    @Test
    public void updateLostCoinsWorks(){
        LocalPlayer localPlayer = new LocalPlayer();

        Coin a = new Coin(1, 1, 1);
        Coin b = new Coin(2, 1, 1);
        Coin c = new Coin(3, 1, 1);
        Coin d = new Coin(4, 1, 1);
        Coin e = new Coin(5, 1, 1);

        ArrayList<Coin> availableCoins = new ArrayList<Coin>();

        availableCoins.add(a);
        availableCoins.add(b);
        availableCoins.add(c);
        availableCoins.add(e);

        localPlayer.addLostCoin(a);
        localPlayer.addLostCoin(b);
        localPlayer.addLostCoin(c);
        localPlayer.addLostCoin(d);

        localPlayer.updateLostCoins(availableCoins);

        ArrayList<Coin> expected = new ArrayList<>();
        expected.add(a);
        expected.add(b);
        expected.add(c);

        assertThat(localPlayer.getLostCoins(), is(expected));

    }

    @Test(expected = IllegalArgumentException.class)
    public void updateLocallyAvailableCoinsWorksOnWrongArgument1(){
        LocalPlayer localPlayer = new LocalPlayer();
        ArrayList<Coin> availableCoins = new ArrayList<Coin>();
        localPlayer.updateLocallyAvailableCoins(availableCoins, true, null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void updateLocallyAvailableCoinsWorksOnWrongArgument2(){
        LocalPlayer localPlayer = new LocalPlayer();
        localPlayer.updateLocallyAvailableCoins(null, false, null);


    }

    @Test(expected = IllegalArgumentException.class)
    public void updateLocallyAvailableCoinsWorksOnWrongArgument3(){
        LocalPlayer localPlayer = new LocalPlayer();
        ArrayList<Coin> availableCoins = new ArrayList<Coin>();
        availableCoins.add(null);
        localPlayer.updateLocallyAvailableCoins(availableCoins, false, null);

    }

    @Test
    public void updateLocallyAvailableCoinsWorksNotLocally(){
        LocalPlayer localPlayer = new LocalPlayer();

        Coin a = new Coin(1, 1, 1);
        Coin b = new Coin(2, 1, 1);
        Coin c = new Coin(3, 1, 1);
        Coin d = new Coin(4, 1, 1);
        Coin e = new Coin(5, 1, 1);

        ArrayList<Coin> availableCoins = new ArrayList<Coin>();

        availableCoins.add(a);
        availableCoins.add(b);
        availableCoins.add(c);
        availableCoins.add(e);

        localPlayer.addLostCoin(a);
        localPlayer.addLostCoin(b);
        localPlayer.addLostCoin(c);
        localPlayer.addLostCoin(d);

        localPlayer.updateLocallyAvailableCoins(availableCoins, false, null);

        ArrayList<Coin> expected1 = new ArrayList<>();
        expected1.add(a);
        expected1.add(b);
        expected1.add(c);

        ArrayList<Coin> expected2 = new ArrayList<>();
        expected2.add(e);

        assertThat(localPlayer.getLostCoins(), is(expected1));
        assertThat(localPlayer.getLocallyAvailableCoins(), is(expected2));

    }


    @Test
    public void updateLocallyAvailableCoinsWorksLocally(){
        LocalPlayer localPlayer = new LocalPlayer();

        Coin a = new Coin(1, 1, 1);
        Coin b = new Coin(2, 1, 1);
        Coin c = new Coin(3, 1, 1);

        ArrayList<Coin> availableCoins = new ArrayList<Coin>();

        availableCoins.add(a);
        availableCoins.add(b);

        localPlayer.setLocallyAvailableCoins(availableCoins);

        localPlayer.addLostCoin(a);

        localPlayer.updateLocallyAvailableCoins(availableCoins, true, b);

        ArrayList<Coin> expected1 = new ArrayList<>();
        expected1.add(a);
        expected1.add(b);

        ArrayList<Coin> expected2 = new ArrayList<>();
        expected2.add(a);

        assertThat(localPlayer.getLostCoins(), is(expected1));
        assertThat(localPlayer.getLocallyAvailableCoins(), is(expected2));

    }
}
