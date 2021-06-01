package sdp.moneyrun.database;

import android.location.Location;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.database.game.GameDatabaseProxy;
import sdp.moneyrun.game.Game;
import sdp.moneyrun.map.Coin;
import sdp.moneyrun.database.riddle.Riddle;
import sdp.moneyrun.player.Player;

public class GameDataBaseProxyInstrumentedTests {

    @Test(expected = IllegalArgumentException.class)
    public void removeGameListenerThrowsExceptionForNullGame(){
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        gdp.removeGameListener(null, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeGameListenerThrowsExceptionForNullListener(){
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        String name = "name";
        Player host = new Player("3", "Bob", 0);
        int maxPlayerCount = 3;
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(0., 0., 1));
        Location location = new Location("LocationManager#GPS_PROVIDER");
        location.setLatitude(10);
        location.setLongitude(20);

        Game game = new Game(name, host, maxPlayerCount, riddles, coins, location, true, 1, 1, 1);
       gdp.removeGameListener(game, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeGameThrowsExceptionForNullGame(){
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        gdp.removeGame(null);
    }
}
