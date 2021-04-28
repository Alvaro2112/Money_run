package sdp.moneyrun.player;

import org.junit.Test;
import java.util.ArrayList;
import sdp.moneyrun.map.Coin;

import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class LocalPlayerTest {

    @Test
    public void addLostCoinWorks() {

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
    public void addCollectedCoinFailsOnNullCoin() {
        LocalPlayer localPlayer = new LocalPlayer();
        localPlayer.addCollectedCoin(null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void addlocallyAvailableCoinsFailsOnNullCoin() {
        LocalPlayer localPlayer = new LocalPlayer();
        localPlayer.addLocallyAvailableCoin(null);

    }

    @Test
    public void addlocallyAvailableCoinsWorks() {
        LocalPlayer localPlayer = new LocalPlayer();
        Coin a = new Coin(1, 1, 1);
        Coin b = new Coin(2, 1, 1);
        ArrayList<Coin> c = new ArrayList<Coin>();
        c.add(a);
        c.add(b);
        localPlayer.addLocallyAvailableCoin(a);
        localPlayer.addLocallyAvailableCoin(b);

        assertThat(localPlayer.getLocallyAvailableCoins(), is(c));

    }


    @Test
    public void addLostCollectedCoinWorks() {

        LocalPlayer localPlayer = new LocalPlayer();
        Coin a = new Coin(1, 1, 1);
        Coin b = new Coin(2, 1, 1);
        ArrayList<Coin> c = new ArrayList<Coin>();
        c.add(a);
        c.add(b);
        localPlayer.addCollectedCoin(a);
        localPlayer.addCollectedCoin(b);

        assertThat(localPlayer.getCollectedCoins(), is(c));

    }

    @Test(expected = IllegalArgumentException.class)
    public void addLostCoinFailsOnNullCoin() {
        LocalPlayer localPlayer = new LocalPlayer();
        localPlayer.addLostCoin(null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void updateLostCoinsWorksOnWrongArgument1() {
        LocalPlayer localPlayer = new LocalPlayer();
        localPlayer.updateLostCoins(null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void updateLostCoinsWorksOnWrongArgument2() {
        LocalPlayer localPlayer = new LocalPlayer();
        ArrayList<Coin> c = new ArrayList<Coin>();
        c.add(null);
        localPlayer.updateLostCoins(c);

    }

    @Test
    public void updateLostCoinsWorks() {
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
    public void updateCoinsWorksOnWrongArgument() {
        LocalPlayer localPlayer = new LocalPlayer();
        localPlayer.updateCoins(null, true);

    }

    @Test(expected = IllegalArgumentException.class)
    public void syncAvailableCoinsFromDbWorksOnWrongArgument1() {
        LocalPlayer localPlayer = new LocalPlayer();
        localPlayer.syncAvailableCoinsFromDb(null);


    }

    @Test(expected = IllegalArgumentException.class)
    public void syncAvailableCoinsFromDbWorksOnWrongArgument2() {
        LocalPlayer localPlayer = new LocalPlayer();
        ArrayList<Coin> availableCoins = new ArrayList<Coin>();
        availableCoins.add(null);
        localPlayer.syncAvailableCoinsFromDb(availableCoins);

    }

    @Test
    public void updateLocallyAvailableCoinsWorksNotLocally() {
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

        localPlayer.syncAvailableCoinsFromDb(availableCoins);

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
    public void updateLocallyAvailableCoinsWorksLocally() {
        LocalPlayer localPlayer = new LocalPlayer();

        Coin a = new Coin(1, 1, 1);
        Coin b = new Coin(2, 1, 1);

        ArrayList<Coin> availableCoins = new ArrayList<Coin>();

        availableCoins.add(a);
        availableCoins.add(b);

        localPlayer.setLocallyAvailableCoins(availableCoins);

        localPlayer.addLostCoin(a);

        localPlayer.updateCoins(b, false);

        ArrayList<Coin> expected1 = new ArrayList<>();
        expected1.add(a);
        expected1.add(b);

        ArrayList<Coin> expected2 = new ArrayList<>();
        expected2.add(a);

        assertThat(localPlayer.getLostCoins(), is(expected1));
        assertThat(localPlayer.getLocallyAvailableCoins(), is(expected2));

    }

    @Test
    public void sendToDbWorks(){
        LocalPlayer localPlayer = new LocalPlayer();

        Coin a = new Coin(1, 1, 1);
        Coin b = new Coin(2, 1, 1);
        Coin c = new Coin(3, 1, 1);

        ArrayList<Coin> availableCoins = new ArrayList<Coin>();

        availableCoins.add(a);
        availableCoins.add(b);

        localPlayer.setLocallyAvailableCoins(availableCoins);

        localPlayer.addLostCoin(a);
        localPlayer.addLostCoin(c);

        ArrayList<Coin> expected = new ArrayList<>();
        expected.add(a);
        expected.add(b);
        expected.add(c);

        assertThat(localPlayer.toSendToDb(), containsInAnyOrder(a, b, c));

    }
}
