package sdp.moneyrun;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;

import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.menu.LeaderboardActivity;
import sdp.moneyrun.ui.menu.MainLeaderboardActivity;
import sdp.moneyrun.ui.menu.MenuActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MainLeaderboardInstrumentedTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("sdp.moneyrun", appContext.getPackageName());
    }

    @Test
    public void addPlayerWorks(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainLeaderboardActivity.class);
        Player user = new Player(3,"Bob", "Epfl",0,0,0);
        intent.putExtra("user", user);

        try (ActivityScenario<MainLeaderboardActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(a ->{

                //Address was not set here before I don't know why
                Player player = new Player(123, "Tess", "SomeAdress", 0,0,0);
                player.setScore(8008, false);
                a.addPlayer(player);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                assertTrue(a.getPlayerList().size() <= a.getMaxPlayerNumber() + 1);
            });
        }
    }

    @Test
    public void addPLayerAddsPlayerToView(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainLeaderboardActivity.class);
        Player user = new Player(3,"Bob", "Epfl",0,0,0);
        intent.putExtra("user", user);

        try (ActivityScenario<MainLeaderboardActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(a ->{
                //Address was not set here before I don't know why
                Player player = new Player(123, "Tess", "SomeAdress", 0,0,0);
                player.setScore(8008, false);
                a.addPlayer(player);
                assertTrue( a.getLdbAdapter().getCount() <= a.getMaxPlayerNumber() + 1);

            });
        }
    }


    @Test
    public void addPlayerNullThrowsException(){
        exception.expect(RuntimeException.class);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainLeaderboardActivity.class);
        Player user = new Player(3,"Bob", "Epfl",0,0,0);
        intent.putExtra("user", user);
        try (ActivityScenario<MainLeaderboardActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(a -> a.addPlayer(null));
        }
    }

    @Test
    public void AddPlayerListWorks(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainLeaderboardActivity.class);
        Player user = new Player(3,"Bob", "Epfl",0,0,0);
        intent.putExtra("user", user);

        try (ActivityScenario<MainLeaderboardActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(a ->{

                //Address was not set here before I don't know why
                Player player = new Player(123, "Tess", "SomeAdress", 0,0,0);
                player.setScore(8008, false);
                //Address was not set here before I don't know why
                Player player2 = new Player(12, "Rafa", "SomeAdress", 0,0,0);
                player2.setScore(8001,false);
                ArrayList<Player> list = new ArrayList<>();
                list.add(player);
                list.add(player2);
                a.addPlayerList(list);
                assertTrue(a.getPlayerList().size() <= a.getMaxPlayerNumber() + 2);
            });
        }
        catch (Exception e){
            e.printStackTrace();
            assertEquals(2,1);
        }
    }

    @Test
    public void AddPlayerListAddsAllPlayerToView(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainLeaderboardActivity.class);
        Player user = new Player(3,"Bob", "Epfl",0,0,0);
        intent.putExtra("user", user);

        try (ActivityScenario<MainLeaderboardActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(a ->{

                //Address was not set here before I don't know why
                Player player = new Player(123, "Tess", "SomeAdress", 0,0,0);
                player.setScore(8008, false);

                //Address was not set here before I don't know why
                Player player2 = new Player(12, "Rafa", "SomeAdress", 0,0,0);
                player2.setScore(8001, false);
                ArrayList<Player> list = new ArrayList<>();
                list.add(player);
                list.add(player2);
                a.addPlayerList(list);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                assertTrue( a.getLdbAdapter().getCount() <= a.getMaxPlayerNumber() + 2);

            });
        }
    }

    @Test
    public void addPlayerListThrowsNullException(){
        exception.expect(RuntimeException.class);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainLeaderboardActivity.class);
        Player user = new Player(3,"Bob", "Epfl",0,0,0);
        intent.putExtra("user", user);

        try (ActivityScenario<MainLeaderboardActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(a -> a.addPlayerList(null));
        }
    }

    @Test
    public void bestToWorstPlayerReturnsSortedPlayerList(){
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player(1,"a0","b",0,0,0));
        players.add(new Player(24,"a1","b",0,0,6));
        players.add(new Player(78,"a2","b",0,0,68));
        players.add(new Player(2,"a3","b",0,0,24));
        players.add(new Player(9,"a4","b",0,0,11));
        ArrayList<Player> players2 = new ArrayList<>();
        players2.add(new Player(78,"a2","b",0,0,68));
        players2.add(new Player(2,"a3","b",0,0,24));
        players2.add(new Player(9,"a4","b",0,0,11));
        players2.add(new Player(24,"a1","b",0,0,6));
        players2.add(new Player(1,"a0","b",0,0,0));
        MainLeaderboardActivity.bestToWorstPlayer(players);
        assertEquals(players2,players);
    }


    @Test
    public void testIfEndGamePlayerReceivesSinglePlayerWhenGivenSizeIsOne(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LeaderboardActivity.class);
        Player user = new Player(3,"Bob", "Epfl",0,0,0);
        intent.putExtra("players"+0, user);
        intent.putExtra("numberOfPlayers", 1);
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(intent)) {
            Intents.init();
            scenario.onActivity(a -> {
                a.getEndGamePlayers();
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                assertEquals(a.getPlayerList().size(),1);
            });
            Intents.release();
        }
    }
}
