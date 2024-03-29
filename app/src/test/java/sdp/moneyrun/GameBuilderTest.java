package sdp.moneyrun;

import android.location.Location;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.game.GameBuilder;
import sdp.moneyrun.player.Player;

import static org.junit.Assert.assertThrows;

public class GameBuilderTest {

    @Test
    public void buildThrowsExceptionWhenNullAttributes() {
        GameBuilder gb = new GameBuilder();
        assertThrows(IllegalStateException.class, gb::build);
        gb.setName("game test");
        assertThrows(IllegalStateException.class, gb::build);
        Player host = new Player("1", "James", 4);
        gb.setHost(new Player("1", "James", 4));
        assertThrows(IllegalStateException.class, gb::build);
        gb.setMaxPlayerCount(1);
        assertThrows(IllegalStateException.class, gb::build);
        gb.setCoins(new ArrayList<>());
        assertThrows(IllegalStateException.class, gb::build);
        gb.setStartLocation(new Location(""));
        assertThrows(IllegalStateException.class, gb::build);
        gb.setRiddles(new ArrayList<>());
        ArrayList<Player> players = new ArrayList<>();
        players.add(host);
        gb.setPlayers(players);
        assertThrows(IllegalStateException.class, gb::build);
        gb.setNumCoins(1);
        assertThrows(IllegalStateException.class, gb::build);
        gb.setRadius(1);
        assertThrows(IllegalStateException.class, gb::build);
    }

    @Test
    public void buildWorksAsExpectedWithNullRiddlesOnly() {
        GameBuilder gb = new GameBuilder();
        Player host = new Player("1", "James", 4);
        List<Player> players = new ArrayList<>();
        players.add(host);

        gb.setName("game test")
                .setHost(host)
                .setMaxPlayerCount(1)
                .setCoins(new ArrayList<>())
                .setStartLocation(new Location(""))
                .setPlayers(players)
                .setNumCoins(1)
                .setRadius(10)
                .setDuration(60)
                .build();
    }

    @Test
    public void buildWorksAsExpectedWithNullPlayersOnly() {
        GameBuilder gb = new GameBuilder();
        gb.setName("game test")
                .setHost(new Player("1", "James", 4))
                .setMaxPlayerCount(1)
                .setCoins(new ArrayList<>())
                .setStartLocation(new Location(""))
                .setRiddles(new ArrayList<>())
                .setNumCoins(1)
                .setRadius(10)
                .setDuration(60)
                .build();
    }

    @Test
    public void setMethodsFailWhenWrongArguments() {
        GameBuilder gb = new GameBuilder();
        assertThrows(IllegalArgumentException.class, () -> gb.setName(null));
        assertThrows(IllegalArgumentException.class, () -> gb.setHost(null));
        assertThrows(IllegalArgumentException.class, () -> gb.setMaxPlayerCount(0));
        assertThrows(IllegalArgumentException.class, () -> gb.setPlayers(null));
        assertThrows(IllegalArgumentException.class, () -> gb.setPlayers(new ArrayList<>()));
        assertThrows(IllegalArgumentException.class, () -> gb.setCoins(null));
        assertThrows(IllegalArgumentException.class, () -> gb.setRiddles(null));
        assertThrows(IllegalArgumentException.class, () -> gb.setStartLocation(null));
        assertThrows(IllegalArgumentException.class, () -> gb.setNumCoins(-1));
        assertThrows(IllegalArgumentException.class, () -> gb.setRadius(0));
        assertThrows(IllegalArgumentException.class, () -> gb.setDuration(0));
    }

    @Test
    public void buildWorksAsExpected() {
        GameBuilder gb = new GameBuilder();
        Player host = new Player("1", "James", 4);
        List<Player> players = new ArrayList<>();
        players.add(host);

        gb.setName("game test")
                .setHost(host)
                .setMaxPlayerCount(1)
                .setCoins(new ArrayList<>())
                .setStartLocation(new Location(""))
                .setRiddles(new ArrayList<>())
                .setPlayers(players)
                .setIsVisible(false)
                .setNumCoins(1)
                .setRadius(10)
                .setDuration(60)
                .build();
    }
}
