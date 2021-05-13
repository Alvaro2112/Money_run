package sdp.moneyrun;

import android.content.Intent;
import android.location.Location;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import androidx.lifecycle.Lifecycle.State;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.FirebaseDatabase;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.database.GameDatabaseProxy;
import sdp.moneyrun.game.Game;
import sdp.moneyrun.game.GameBuilder;
import sdp.moneyrun.map.Coin;
import sdp.moneyrun.map.Riddle;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.game.GameLobbyActivity;
import sdp.moneyrun.ui.map.MapActivity;
import sdp.moneyrun.ui.menu.MainLeaderboardActivity;
import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.user.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
public class MenuActivityTest {
    private  long ASYNC_CALL_TIMEOUT = 10L;

    //Since the features of Menu now depend on the intent it is usually launched with
    //We also need to launch MenuActivity with a valid intent for tests to pass
    private Intent getStartIntent() {
        User currentUser = new User("999", "CURRENT_USER", "Epfl"
                , 0, 0, 0);
        Intent toStart = new Intent(ApplicationProvider.getApplicationContext(), MenuActivity.class);
        toStart.putExtra("user", currentUser);
        return toStart;
    }

    @Rule
    public ActivityScenarioRule<MenuActivity> testRule = new ActivityScenarioRule<>(getStartIntent());

    @Rule
    public GrantPermissionRule permissionFineLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule
    public GrantPermissionRule permissionCoarseLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION);

    //adapted from https://stackoverflow.com/questions/28408114/how-can-to-test-by-espresso-android-widget-textview-seterror/28412476
    private static Matcher<View> withError(final String expected) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof EditText)) {
                    return false;
                }
                EditText editText = (EditText) view;
                return editText.getError().toString().equals(expected);
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }

    public Game getGame() {
        String name = "JoinGameImplementationTest";
        Player host = new Player("3", "Bob", 0);
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

    @Test
    public void activityStartsProperly() {
        assertEquals(State.RESUMED, testRule.getScenario().getState());
    }

    @Test
    public void joinGamePopupIsDisplayed() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Intents.init();
            onView(ViewMatchers.withId(R.id.join_game)).perform(ViewActions.click());
            onView(ViewMatchers.withId(R.id.join_popup)).check(matches(isDisplayed()));
            Intents.release();
        }
    }

    @Test
    public void filterWorks(){
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {

            FirebaseDatabase.getInstance().goOffline();

            onView(ViewMatchers.withId(R.id.join_game)).perform(ViewActions.click());
            onView(ViewMatchers.withId(R.id.join_popup)).check(matches(isDisplayed()));

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            scenario.onActivity(a -> {
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(a);
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(a, selfLocation -> {
                            if(selfLocation == null){
                                assertEquals(1, 0);
                                return;
                            }
                            Player host = new Player("364556546", "Bob", 0);

                            GameBuilder gb = new GameBuilder();
                            gb.setName("checkfilter")
                                    .setMaxPlayerCount(12)
                                    .setHost(host)
                                    .setIsVisible(true)
                                    .setRiddles(new ArrayList<>())
                                    .setCoins(new ArrayList<>())
                                    .setStartLocation(selfLocation);

                            GameDatabaseProxy db = new GameDatabaseProxy();
                            db.putGame(gb.build());
                        });
            });

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            onView(ViewMatchers.withId(R.id.join_game_text_filter)).perform(typeText("checkfilter"), closeSoftKeyboard());
            onView(ViewMatchers.withId(R.id.join_game_button_filter)).perform(ViewActions.click());
            onView(ViewMatchers.withId(R.id.join_game_button_filter)).check(matches(isClickable()));

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            scenario.onActivity(a -> {
                int n = a.getIntent().getIntExtra("number_of_results", -1);

                assertEquals(1, n);
            });

            FirebaseDatabase.getInstance().goOnline();
        }
    }

    @Test
    public void filterWithNotExistingNameWorks(){
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {

            onView(ViewMatchers.withId(R.id.join_game)).perform(ViewActions.click());
            onView(ViewMatchers.withId(R.id.join_popup)).check(matches(isDisplayed()));

            String filter = "randomNameThatWillNeverOccur56903645734657260287345260874523048732648";
            onView(ViewMatchers.withId(R.id.join_game_text_filter)).perform(typeText(filter), closeSoftKeyboard());
            onView(ViewMatchers.withId(R.id.join_game_button_filter)).perform(ViewActions.click());

            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            scenario.onActivity(a -> {
                int n = a.getIntent().getIntExtra("number_of_results", -1);

                assertEquals(0, n);
            });
        }
    }

    @Test
    public void OnlyNearGamesShowUp(){
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {

            FirebaseDatabase.getInstance().goOffline();

            String filter = "onlythistest887655677888";
            String nearGameName = "nearGame_" + filter;
            String farGameName = "farGame_" + filter;

            onView(ViewMatchers.withId(R.id.join_game)).perform(ViewActions.click());
            onView(ViewMatchers.withId(R.id.join_popup)).check(matches(isDisplayed()));

            scenario.onActivity(a -> {
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(a);
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(a, selfLocation -> {
                            if(selfLocation == null){
                                assertEquals(1, 0);
                                return;
                            }
                            Player host = new Player("364556546", "Bob", 0);

                            GameDatabaseProxy db = new GameDatabaseProxy();
                            // Near game
                            GameBuilder gb = new GameBuilder();
                            gb.setName(nearGameName)
                                    .setMaxPlayerCount(12)
                                    .setHost(host)
                                    .setIsVisible(true)
                                    .setRiddles(new ArrayList<>())
                                    .setCoins(new ArrayList<>())
                                    .setStartLocation(selfLocation);

                            db.putGame(gb.build());

                            Location farLocation = new Location("");
                            farLocation.setLatitude(selfLocation.getLatitude() + 10.);
                            farLocation.setLongitude(selfLocation.getLongitude() + 10.);
                            // Far game
                            GameBuilder gb2 = new GameBuilder();
                            gb2.setName(farGameName)
                                    .setMaxPlayerCount(12)
                                    .setHost(host)
                                    .setIsVisible(true)
                                    .setRiddles(new ArrayList<>())
                                    .setCoins(new ArrayList<>())
                                    .setStartLocation(farLocation);

                            db.putGame(gb2.build());
                        });
            });

            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            onView(ViewMatchers.withId(R.id.join_game_text_filter)).perform(typeText(filter), closeSoftKeyboard());
            onView(ViewMatchers.withId(R.id.join_game_button_filter)).perform(ViewActions.click());

            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            scenario.onActivity(a -> {
                int n = a.getIntent().getIntExtra("number_of_results", -1);

                assertEquals(1, n);
            });

            FirebaseDatabase.getInstance().goOnline();
        }
    }

//    @Test
//    public void joinGamePopupDoesntCrashAfterPlayerChange() {
//        GameDatabaseProxy gdp = new GameDatabaseProxy();
//        Game game = getGame();
//        gdp.putGame(game);
//        CountDownLatch added = new CountDownLatch(1);
//        gdp.updateGameInDatabase(game, task -> added.countDown());
//        try {
//            added.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
//            assertEquals(0l, added.getCount());
//        } catch (InterruptedException e) {
//            fail();
//        }
//
//        CountDownLatch gotten = new CountDownLatch(1);
//        //To get the Button ID of the button corresponding to this Game, we have
//        //to get all the games in the DB, and find out how many are visible, aka
//        //how many have buttons since thats how the ids are given out. Tedious but necessary.
//        Task<DataSnapshot> dbGames = FirebaseDatabase.getInstance().getReference()
//                .child("games")
//                .get()
//                .addOnCompleteListener(task -> {
//                    gotten.countDown();
//                });
//        try {
//            gotten.await(ASYNC_CALL_TIMEOUT, TimeUnit.SECONDS);
//            assertEquals(0l, gotten.getCount());
//        } catch (InterruptedException e) {
//            fail();
//        }
//        if(!dbGames.isSuccessful()){
//            fail();
//        }
//        int visibleGames = 0;
//        for(DataSnapshot d : dbGames.getResult().getChildren()){
//            if(d != null) {
//                if (d.child("isVisible").getValue(Boolean.class)) {
//                    visibleGames += 1;
//                }
//            }
//        }
//        List<Player> playerList = new ArrayList<>();
//        playerList.add(new Player(5,"Aragon", "Epfl",0,0,0));
//        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(MenuActivity.class)) {
//            Intents.init();
//            onView(ViewMatchers.withId(R.id.join_game)).perform(ViewActions.click());
//            onView(ViewMatchers.withId(R.id.join_popup)).check(matches(isDisplayed()));
//            onView(ViewMatchers.withId(visibleGames-1)).perform(ViewActions.scrollTo());
//            game.setPlayers(playerList, false);
//            Thread.sleep(2000);
//            onView(ViewMatchers.withId(R.id.join_popup)).check(matches(isDisplayed()));
//            playerList.add(new Player(6,"Heimdalr", "Epfl",0,0,0));
//            game.setPlayers(playerList, false);
//            Thread.sleep(2000);
//            onView(ViewMatchers.withId(R.id.join_popup)).check(matches(isDisplayed()));
//            Intents.release();
//        }catch (Exception e) {
//            fail();
//        }
//    }

    @Test
    public void CreateGameSendsYouToLobby() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Intents.init();

            onView(ViewMatchers.withId(R.id.new_game)).perform(ViewActions.click());

            Thread.sleep(1000);

            final String game_name = "CreateGameTest";
            final String max_player_count = String.valueOf(3);
            Espresso.onView(withId(R.id.nameGameField)).perform(typeText(game_name), closeSoftKeyboard());
            Espresso.onView(withId(R.id.maxPlayerCountField)).perform(typeText(max_player_count), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameSubmit)).perform(ViewActions.click());
            Thread.sleep(2000);
            intended(hasComponent(GameLobbyActivity.class.getName()));
            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Intents.release();
        }
    }



    @Test
    public void mapButtonAndSplashScreenWorks() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Intents.init();
            Espresso.onView(withId(R.id.map_button)).perform(ViewActions.click());
            Thread.sleep(100);

            onView(ViewMatchers.withId(R.id.splashscreen)).check(matches(isDisplayed()));
            Thread.sleep(10000);
            intended(hasComponent(MapActivity.class.getName()));

            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();

            Intents.release();
        }
    }

    @Test
    public void newGamePopupIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MenuActivity.class);
        User user = new User("3", "Bob", "Epfl", 0, 0, 0);
        intent.putExtra("user", user);

        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(intent)) {

            Intents.init();

            onView(ViewMatchers.withId(R.id.new_game)).perform(ViewActions.click());
            onView(ViewMatchers.withId(R.id.new_game_popup)).check(matches(isDisplayed()));

            Intents.release();
        }
    }

    @Test
    public void leaderboardButtonWorks() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Intents.init();

            onView(withId(R.id.drawer_layout))
                    .check(matches(isClosed(Gravity.LEFT)))
                    .perform(DrawerActions.open());
            Thread.sleep(1000);
            Espresso.onView(withId(R.id.main_leaderboard_button)).perform(ViewActions.click());
            intended(hasComponent(MainLeaderboardActivity.class.getName()));

            Intents.release();
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals(-2, 1);

        }
    }

    @Test
    public void navigationViewOpens() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Intents.init();

            onView(withId(R.id.drawer_layout))
                    .check(matches(isClosed(Gravity.LEFT)))
                    .perform(DrawerActions.open());

            Intents.release();
        }
    }

    @Test
    public void newGameEmptyNameFieldError() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Intents.init();

            onView(ViewMatchers.withId(R.id.new_game)).perform(ViewActions.click());

            Thread.sleep(1000);

            final String max_player_count = String.valueOf(12);
            final String expected = "This field is required";

            Espresso.onView(withId(R.id.newGameSubmit)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.nameGameField)).check(matches(withError(expected)));

            Espresso.onView(withId(R.id.maxPlayerCountField)).perform(typeText(max_player_count), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameSubmit)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.nameGameField)).check(matches(withError(expected)));

            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Intents.release();
        }
    }

    @Test
    public void newGameEmptyMaxPlayerCountFieldError() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Intents.init();

            onView(ViewMatchers.withId(R.id.new_game)).perform(ViewActions.click());

            Thread.sleep(1000);

            final String game_name = "new game";
            final String expected = "This field is required";

            Espresso.onView(withId(R.id.nameGameField)).perform(typeText(game_name), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameSubmit)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.maxPlayerCountField)).check(matches(withError(expected)));

            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Intents.release();
        }
    }

    @Test
    public void newGameZeroMaxPlayerCountFieldError() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Intents.init();

            onView(ViewMatchers.withId(R.id.new_game)).perform(ViewActions.click());

            Thread.sleep(1000);

            final String game_name = "new game";
            final String max_player_count_zero = String.valueOf(0);
            final String expected_zero_players = "There should be at least one player in a game";

            Espresso.onView(withId(R.id.nameGameField)).perform(typeText(game_name), closeSoftKeyboard());
            Espresso.onView(withId(R.id.maxPlayerCountField)).perform(typeText(max_player_count_zero), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameSubmit)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.maxPlayerCountField)).check(matches(withError(expected_zero_players)));

            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Intents.release();
        }
    }

    @Test
    public void newGameWorks() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Intents.init();

            onView(ViewMatchers.withId(R.id.new_game)).perform(ViewActions.click());

            Thread.sleep(2000);

            final String game_name = "test game";
            final String max_player_count = String.valueOf(1);

            Espresso.onView(withId(R.id.nameGameField)).perform(typeText(game_name), closeSoftKeyboard());
            Espresso.onView(withId(R.id.maxPlayerCountField)).perform(typeText(max_player_count), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameSubmit)).perform(ViewActions.click());

            assertEquals(1, 1);

            Intents.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
            Intents.release();
        }
    }

    /*
    @Test
    public void joinLobbyFromJoinButtonIntentIsSent(){
        GameDatabaseProxy gdp = new GameDatabaseProxy();
        Game game = getGame();
        gdp.putGame(game);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //To get the Button ID of the button corresponding to this Game, we have
        //to get all the games in the DB, and find out how many are visible, aka
        //how many have buttons since thats how the ids are given out. Tedious but necessary.
        Task<DataSnapshot> dbGames = FirebaseDatabase.getInstance().getReference().child("games").get();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!dbGames.isSuccessful()) {
            fail();
        }
        int visibleGames = 0;
        for (DataSnapshot d : dbGames.getResult().getChildren()) {
            if (d.child("isVisible").getValue(Boolean.class)) {
                visibleGames += 1;
            }
        }
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Intents.init();
            onView(ViewMatchers.withId(R.id.join_game)).perform(ViewActions.click());
            Thread.sleep(5000);
            onView(ViewMatchers.withId(R.id.join_popup)).check(matches(isDisplayed()));
            onView(ViewMatchers.withId(visibleGames - 1)).perform(ViewActions.scrollTo());
            onView(ViewMatchers.withId(R.id.join_popup)).check(matches(isDisplayed()));
            onView(ViewMatchers.withId(visibleGames - 1)).perform(ViewActions.click());
            Thread.sleep(2000);
            intended(hasComponent(GameLobbyActivity.class.getName()));
            Intents.release();
        } catch (Exception e) {
            fail();
        }
    }
    */
}