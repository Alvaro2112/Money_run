package sdp.moneyrun.player;

import org.junit.Test;

public class PlayerTest {

    @Test(expected = IllegalArgumentException.class)
    public void playerConstructorFailsCorrectly1() {
        Player player = new Player(null, "bob", 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void playerConstructorFailsCorrectly2() {
        Player player = new Player("123", null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void playerConstructorFailsCorrectly3() {
        System.out.println("".isEmpty());
        Player player = new Player("123", "", 1);
    }
}
