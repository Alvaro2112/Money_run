package sdp.moneyrun;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.database.FirebaseDatabase;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;

import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.MainActivity;
import sdp.moneyrun.ui.menu.LeaderboardActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LeaderboardInstrumentedTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setPersistence() {
        if (!MainActivity.calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            MainActivity.calledAlready = true;
        }
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("sdp.moneyrun", appContext.getPackageName());
    }

    @Test
    public void addPlayerWorks() {
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a -> {

                //Address was not set here before I don't know why
                Player player = new Player("123", "Tess", 0);
                player.setScore(8008, false);
                a.addPlayer(player);

                assertEquals(a.getPlayerList().size(), 6);
            });
        } catch (Exception e) {
            assertEquals(2, 1);
        }
    }

    @Test
    public void addPLayerAddsPlayerToView() {
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a -> {
                //Address was not set here before I don't know why
                Player player = new Player("123", "Tess", 0);
                player.setScore(8008, false);
                a.addPlayer(player);
                assertEquals(a.getLdbAdapter().getCount(), 6);

            });
        }
    }


    @Test
    public void addPlayerNullThrowsException() {
        exception.expect(RuntimeException.class);
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a -> a.addPlayer(null));
        }
    }

    @Test
    public void AddPlayerListWorks() {
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a -> {

                //Address was not set here before I don't know why
                Player player = new Player("123", "Tess", 0);
                player.setScore(8008, false);
                //Address was not set here before I don't know why
                Player player2 = new Player("12", "Rafa", 0);
                player2.setScore(8001, false);
                ArrayList<Player> list = new ArrayList<>();
                list.add(player);
                list.add(player2);
                a.addPlayerList(list);
                assertEquals(a.getPlayerList().size(), 7);
            });
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals(2, 1);
        }
    }

    @Test
    public void AddPlayerListAddsAllPlayerToView() {
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a -> {

                //Address was not set here before I don't know why
                Player player = new Player("123", "Tess", 0);
                player.setScore(8008, false);

                //Address was not set here before I don't know why
                Player player2 = new Player("12", "Rafa", 0);
                player2.setScore(8001, false);
                ArrayList<Player> list = new ArrayList<>();
                list.add(player);
                list.add(player2);
                a.addPlayerList(list);
                assertEquals(a.getLdbAdapter().getCount(), 7);

            });
        }
    }

    @Test
    public void addPlayerListThrowsNullException() {
        exception.expect(RuntimeException.class);
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a -> a.addPlayerList(null));
        }
    }

    @Test
    public void testIfOneDummyPlayerIsSet() {
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a -> {
                boolean check = false;
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                    assertEquals(-2, 1);
                }
                for (Player p : a.getPlayerList()) {
                    if (p.getName().equals("Chris")) {
                        check = true;
                        break;
                    }
                }
                assertTrue(check);
            });
        }
    }

    @Test
    public void setMainPlayerGetsTheRightInfoAndSetsThePlayerAttributes() {
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a -> {
                String playerId = "48390";
                String name = "John";
                String address = "Here";
                Player user = new Player(playerId);
                user.setName(name);

                Intent intent = new Intent(a, LeaderboardActivity.class);
                intent.putExtra("user", user);

                a.setMainPlayer(user);
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Player p = a.getUserPlayer();
                assertNotNull(p);
                assertNotNull(p.getName());
            });
        }
    }

    @Test
    public void bestToWorstPlayerReturnsSortedPlayerList() {
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("1", "a0", 0));
        players.add(new Player("24", "a1", 6));
        players.add(new Player("78", "a2", 68));
        players.add(new Player("2", "a3", 24));
        players.add(new Player("9", "a4", 11));
        ArrayList<Player> players2 = new ArrayList<>();
        players2.add(new Player("78", "a2", 68));
        players2.add(new Player("2", "a3", 24));
        players2.add(new Player("9", "a4", 11));
        players2.add(new Player("24", "a1", 6));
        players2.add(new Player("1", "a0", 0));
        LeaderboardActivity.bestToWorstPlayer(players);
        assertEquals(players2, players);
    }


    @Test
    public void testIfEndGamePlayerReceivesSinglePlayerWhenGivenSizeIsOne() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LeaderboardActivity.class);
        Player user = new Player("3", "Bob", 0);
        intent.putExtra("players" + 0, user);
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
                assertEquals(a.getPlayerList().size(), 1);
            });
            Intents.release();
        }
    }
}
