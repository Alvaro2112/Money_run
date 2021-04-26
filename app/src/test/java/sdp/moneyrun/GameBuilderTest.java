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
    public void buildThrowsExceptionWhenNullAttributes(){
        GameBuilder gb = new GameBuilder();
        assertThrows(IllegalStateException.class, gb::build);
        gb.setName("game test");
        assertThrows(IllegalStateException.class, gb::build);
        gb.setHost(new Player(1, "James", "Lausanne", 3, 4));
        assertThrows(IllegalStateException.class, gb::build);
        gb.setMaxPlayerCount(1);
        assertThrows(IllegalStateException.class, gb::build);
        gb.setCoins(new ArrayList<>());
        assertThrows(IllegalStateException.class, gb::build);
        gb.setStartLocation(new Location(""));
        assertThrows(IllegalStateException.class, gb::build);
    }

    @Test
    public void buildWorksAsExpectedWithNullRiddlesOnly(){
        GameBuilder gb = new GameBuilder();
        gb.setName("game test");
        gb.setHost(new Player(1, "James", "Lausanne", 3, 4));
        gb.setMaxPlayerCount(1);
        gb.setCoins(new ArrayList<>());
        gb.setStartLocation(new Location(""));
        gb.setPlayers(new ArrayList<>());
        gb.build();
    }

    @Test
    public void buildWorksAsExpectedWithNullPlayersOnly(){
        GameBuilder gb = new GameBuilder();
        gb.setName("game test");
        gb.setHost(new Player(1, "James", "Lausanne", 3, 4));
        gb.setMaxPlayerCount(1);
        gb.setCoins(new ArrayList<>());
        gb.setStartLocation(new Location(""));
        gb.setRiddles(new ArrayList<>());
        gb.build();
    }

    @Test
    public void buildWorksAsExpected(){
        GameBuilder gb = new GameBuilder();
        gb.setName("game test");
        Player host = new Player(1, "James", "Lausanne", 3, 4);
        gb.setHost(host);
        gb.setMaxPlayerCount(1);
        gb.setCoins(new ArrayList<>());
        gb.setStartLocation(new Location(""));
        gb.setRiddles(new ArrayList<>());
        List<Player> players = new ArrayList<>();
        players.add(host);
        gb.setPlayers(players);
        gb.setIsVisible(false);
        gb.build();
    }
}
