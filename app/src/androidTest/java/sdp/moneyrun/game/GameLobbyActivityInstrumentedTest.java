package sdp.moneyrun.game;

import android.content.Intent;
import android.location.Location;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.database.FirebaseDatabase;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.R;
import sdp.moneyrun.database.GameDatabaseProxy;
import sdp.moneyrun.map.Coin;
import sdp.moneyrun.map.Riddle;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.MainActivity;
import sdp.moneyrun.ui.game.GameLobbyActivity;
import sdp.moneyrun.ui.map.MapActivity;
import sdp.moneyrun.ui.menu.MenuActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class GameLobbyActivityInstrumentedTest {

    @BeforeClass
    public static void setPersistence(){
        if(!MainActivity.calledAlready){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            MainActivity.calledAlready = true;
        }
    }


    private Intent getStartIntent() {
        Player currentUser = new Player(999, "CURRENT_USER",0);
        Intent toStart = new Intent(ApplicationProvider.getApplicationContext(), GameLobbyActivity.class);
        toStart.putExtra("currentUser", currentUser);
        return  toStart;
    }

    public Game getGame(){
        String name = "LobbyActivityInstrumentedTest";
        Player host = new Player(3,"Bob",0);
        int maxPlayerCount = 2;
        List<Riddle> riddles = new ArrayList<>();
        riddles.add(new Riddle("yes?", "blue", "green", "yellow", "brown", "a"));
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(0., 0., 1));
        Location location = new Location("LocationManager#GPS_PROVIDER");
        location.setLatitude(37.4219473);
        location.setLongitude(-122.0840015);
        return new Game(name, host, maxPlayerCount, riddles, coins, location, true);
    }


   /* @Rule
    public ActivityScenarioRule<GameLobbyActivity> testRule = new ActivityScenarioRule<>(GameLobbyActivity.class);
*/
    @Test
    public void activityStartsProperly() {
        //assertEquals(Lifecycle.State.RESUMED, testRule.getScenario().getState());
    }

    @Test
    public void StartGameAsHostWorks() {
        Player host = new Player(3,"Bob",0);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), GameLobbyActivity.class);
        intent.putExtra("currentUser", host);
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        Game game = getGame();

        List<Player> players = game.getPlayers();
        players.add(host);

        String id = gdp.putGame(game);
        System.out.println(id);

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        intent.putExtra("currentGameId", id);

        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(intent)) {
            Intents.init();
            Thread.sleep(4000);
            onView(ViewMatchers.withId(R.id.launch_game_button)).perform(ViewActions.click());
            Thread.sleep(4000);
            intended(hasComponent(MapActivity.class.getName()));
            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    @Test
    public void nameDisplaysProperly(){
        Intent intent = getStartIntent();
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        Game game = getGame();
        String id = gdp.putGame(game);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        intent.putExtra("currentGameId", id);
        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(intent)) {
            Intents.init();
            onView(ViewMatchers.withId(R.id.lobby_title)).check(matches(withText(game.getName())));
            Intents.release();
        }
    }

    @Test
    public void playerListUpdatesWithDB(){
        Intent intent = getStartIntent();
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        Game game = getGame();
        Player justJoined = new Player(3,"justJoined",0);
        List<Player> players = game.getPlayers();
        players.add(justJoined);

        String id = gdp.putGame(game);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        intent.putExtra("currentGameId", id);
        intent.putExtra("currentUser", justJoined);
        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(intent)) {
            Intents.init();
            Thread.sleep(4000);
            onView(ViewMatchers.withId(R.id.player_list_textView)).check(matches(withText(game.getHost().getName())));
            game.setPlayers(players, false);
            Thread.sleep(4000);
            onView(ViewMatchers.withId(R.id.player_list_textView)).check(matches(withText(game.getHost().getName()+"\n"+justJoined.getName())));
            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void playersMissingDisplaysProperly(){
        Intent intent = getStartIntent();
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        Game game = getGame();
        String id = gdp.putGame(game);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        intent.putExtra("currentGameId", id);
        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(intent)) {
            Intents.init();
            Thread.sleep(2000);
            onView(ViewMatchers.withId(R.id.players_missing_TextView))
                    .check(matches(withText("Players missing: "
                            + (game.getMaxPlayerCount() - 1))));
            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void playersMissingUpdatesWithDB(){
        Intent intent = getStartIntent();
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        Game game = getGame();
        Player justJoined = new Player(3,"justJoined",0);
        List<Player> players = game.getPlayers();
        players.add(justJoined);


        String id = gdp.putGame(game);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        intent.putExtra("currentGameId", id);
        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(intent)) {
            Intents.init();
            Thread.sleep(2000);
            onView(ViewMatchers.withId(R.id.players_missing_TextView))
                    .check(matches(withText("Players missing: "
                            + (game.getMaxPlayerCount() - 1))));

            game.setPlayers(players, false);
            Thread.sleep(2000);
            onView(ViewMatchers.withId(R.id.players_missing_TextView))
                    .check(matches(withText("Players missing: "
                            + (game.getMaxPlayerCount() - 2))));
            Intents.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void LeaveLobbyWorks() {
        Intent intent = getStartIntent();
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        Game game = getGame();
        String id = gdp.putGame(game);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        intent.putExtra("currentGameId", id);
        intent.putExtra("currentUser", new Player(1234567891, "alex", 0));



        try (ActivityScenario<GameLobbyActivity> scenario = ActivityScenario.launch(intent)) {
            Intents.init();
            onView(ViewMatchers.withId(R.id.leave_lobby_button)).perform(ViewActions.click());
            Thread.sleep(2000);
            intended(hasComponent(MenuActivity.class.getName()));
            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Intents.release();
        }
    }
}
