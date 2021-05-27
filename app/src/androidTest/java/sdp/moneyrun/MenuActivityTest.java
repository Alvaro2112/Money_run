package sdp.moneyrun;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
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
import sdp.moneyrun.location.AndroidLocationService;
import sdp.moneyrun.location.LocationRepresentation;
import sdp.moneyrun.map.Coin;
import sdp.moneyrun.map.Riddle;
import sdp.moneyrun.menu.JoinGameImplementation;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.player.PlayerBuilder;
import sdp.moneyrun.ui.game.GameLobbyActivity;
import sdp.moneyrun.ui.menu.LeaderboardActivity;
import sdp.moneyrun.ui.menu.MainLeaderboardActivity;
import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.user.User;
import sdp.moneyrun.weather.WeatherForecast;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


@RunWith(AndroidJUnit4.class)
public class MenuActivityTest {
    private  long ASYNC_CALL_TIMEOUT = 10L;

    //Since the features of Menu now depend on the intent it is usually launched with
    //We also need to launch MenuActivity with a valid intent for tests to pass
    private Intent getStartIntent() {
        User currentUser = new User("888", "CURRENT_USER", "Epfl"
                , 0, 0, 0);
        Intent toStart = new Intent(ApplicationProvider.getApplicationContext(), MenuActivity.class);
        toStart.putExtra("user", currentUser);
        return toStart;
    }

    private Location getMockedLocation(){
        Location gameLocation = new Location("");
        gameLocation.setLongitude(12.);
        gameLocation.setLatitude(12.);

        return gameLocation;
    }

    private Location getFarLocation(){
        Location gameLocation = new Location("");
        gameLocation.setLongitude(22.);
        gameLocation.setLatitude(22.);

        return gameLocation;
    }

    private Game addFirstGameToDatabase(){
        // Define game location
        Location gameLocation = getMockedLocation();
        Location farLocation = getFarLocation();

        // Define game host
        User userHost = new User("777", "CURRENT_USER", "Epfl", 0, 0, 0);
        PlayerBuilder hostBuilder = new PlayerBuilder();
        Player host = hostBuilder.setPlayerId(userHost.getUserId())
                .setName(userHost.getName())
                .setScore(0)
                .build();

        List<Player> players = new ArrayList<>();
        players.add(host);

        // Define game
        GameDatabaseProxy gdb = new GameDatabaseProxy();
        GameBuilder gb = new GameBuilder();
        Game game = gb.setName("Near game")
                .setHost(host)
                .setMaxPlayerCount(10)
                .setStartLocation(gameLocation)
                .setIsVisible(true)
                .setCoins(new ArrayList<>())
                .setPlayers(players)
                .setRiddles(new ArrayList<>())
                .setNumCoins(10)
                .setRadius(10)
                .setDuration(999999)
                .build();
        game.setId(host.getPlayerId());
        gdb.putGame(game);

        return game;
    }

    private Game addSecondGameToDatabase(){
        // Define game location
        Location farLocation = getFarLocation();

        // Define game host
        User userHost = new User("888", "CURRENT_USER", "Epfl", 0, 0, 0);
        PlayerBuilder hostBuilder = new PlayerBuilder();
        Player host = hostBuilder.setPlayerId(userHost.getUserId())
                .setName(userHost.getName())
                .setScore(0)
                .build();

        List<Player> players = new ArrayList<>();
        players.add(host);

        // Define game
        GameDatabaseProxy gdb = new GameDatabaseProxy();
        GameBuilder gb = new GameBuilder();
        Game game = gb.setName("Far game")
                .setHost(host)
                .setMaxPlayerCount(10)
                .setStartLocation(farLocation)
                .setIsVisible(true)
                .setCoins(new ArrayList<>())
                .setPlayers(players)
                .setRiddles(new ArrayList<>())
                .setNumCoins(10)
                .setRadius(10)
                .setDuration(999999)
                .build();
        game.setId(host.getPlayerId());
        gdb.putGame(game);

        return game;
    }

    @Rule
    public ActivityScenarioRule<MenuActivity> testRule = new ActivityScenarioRule<>(getStartIntent());

    //adapted from https://stackoverflow.com/questions/28408114/how-can-to-test-by-espresso-android-widget-textview-seterror/28412476
    @NonNull
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



    @NonNull
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
        return new Game(name, host, maxPlayerCount, riddles, coins, location, true, 2, 25, 2);
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
    public void backButtonDoesNothing1(){


        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MenuActivity.class);
        User user = new User("3", "Bob", "Epfl", 0, 0, 0);
        intent.putExtra("user", user);


        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(intent)) {
            assertEquals(Lifecycle.State.RESUMED, scenario.getState());
            onView(isRoot()).perform(ViewActions.pressBack());
            assertEquals(Lifecycle.State.RESUMED, scenario.getState());
        }
    }

    @Test
    public void filterWithNotExistingNameWorks() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {

            onView(ViewMatchers.withId(R.id.join_game)).perform(ViewActions.click());
            onView(ViewMatchers.withId(R.id.join_popup)).check(matches(isDisplayed()));

            String filter = "randomNameThatWillNeverOccur56903645734657260287345260874523048732648";
            onView(ViewMatchers.withId(R.id.join_game_text_filter)).perform(typeText(filter), closeSoftKeyboard());
            onView(ViewMatchers.withId(R.id.join_game_button_filter)).perform(ViewActions.click());

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            onView(ViewMatchers.withId(0)).check(doesNotExist());

        }
    }


    @Test
    public void CreateGameSendsYouToLobby() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Intents.init();

            onView(ViewMatchers.withId(R.id.new_game)).perform(ViewActions.click());

            Thread.sleep(1000);

            final String game_name = "CreateGameTest";
            final String max_player_count = String.valueOf(3);
            final String numCoins = String.valueOf(5);
            final String radius = String.valueOf(25);
            final String duration = String.valueOf(5);

            Espresso.onView(withId(R.id.nameGameField)).perform(typeText(game_name), closeSoftKeyboard());
            Espresso.onView(withId(R.id.maxPlayerCountField)).perform(typeText(max_player_count), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameNumCoins)).perform(typeText(numCoins), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameRadius)).perform(typeText(radius), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameDuration)).perform(typeText(duration), closeSoftKeyboard());

            Espresso.onView(withId(R.id.newGameSubmit)).perform(ViewActions.click());
            Thread.sleep(2000);
            intended(hasComponent(GameLobbyActivity.class.getName()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
           Intents.release();
        }
    }


    @Test
    public void newGamePopupIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MenuActivity.class);
        User user = new User("3", "Bob", "Epfl", 0, 0, 0);
        intent.putExtra("user", user);

        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(intent)) {
            Thread.sleep(1000);
            Intents.init();

            onView(ViewMatchers.withId(R.id.new_game)).perform(ViewActions.click());
            onView(ViewMatchers.withId(R.id.new_game_popup)).check(matches(isDisplayed()));

            Intents.release();
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void leaderboardButtonWorks() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Intents.init();

            onView(withId(R.id.drawer_layout))
                    .check(matches(isClosed(Gravity.LEFT)))
                    .perform(DrawerActions.open());
            Thread.sleep(3000);
            Espresso.onView(withId(R.id.main_leaderboard_button)).perform(ViewActions.click());
            Thread.sleep(3000);

            intended(hasComponent(MainLeaderboardActivity.class.getName()));
            Thread.sleep(3000);

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
    public void newGameEmptyNumCoinFieldError() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Intents.init();

            onView(ViewMatchers.withId(R.id.new_game)).perform(ViewActions.click());

            Thread.sleep(1000);

            final String game_name = "new game";
            final String max_player_count = String.valueOf(12);

            final String expected = "This field is required";

            Espresso.onView(withId(R.id.nameGameField)).perform(typeText(game_name), closeSoftKeyboard());
            Espresso.onView(withId(R.id.maxPlayerCountField)).perform(typeText(max_player_count), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameSubmit)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.newGameNumCoins)).check(matches(withError(expected)));

            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Intents.release();
        }
    }

    @Test
    public void newGameEmptyRadiusFieldError() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Intents.init();

            onView(ViewMatchers.withId(R.id.new_game)).perform(ViewActions.click());

            Thread.sleep(1000);

            final String game_name = "new game";
            final String max_player_count = String.valueOf(12);

            final String expected = "This field is required";

            Espresso.onView(withId(R.id.nameGameField)).perform(typeText(game_name), closeSoftKeyboard());
            Espresso.onView(withId(R.id.maxPlayerCountField)).perform(typeText(max_player_count), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameNumCoins)).perform(typeText(max_player_count), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameSubmit)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.newGameRadius)).check(matches(withError(expected)));

            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Intents.release();
        }
    }

    @Test
    public void newGameEmptyDurationFieldError() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Intents.init();

            onView(ViewMatchers.withId(R.id.new_game)).perform(ViewActions.click());

            Thread.sleep(1000);

            final String game_name = "new game";
            final String max_player_count = String.valueOf(12);

            final String expected = "This field is required";

            Espresso.onView(withId(R.id.nameGameField)).perform(typeText(game_name), closeSoftKeyboard());
            Espresso.onView(withId(R.id.maxPlayerCountField)).perform(typeText(max_player_count), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameNumCoins)).perform(typeText(max_player_count), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameRadius)).perform(typeText(max_player_count), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameSubmit)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.newGameDuration)).check(matches(withError(expected)));

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

            final String max_player_count_zero = String.valueOf(0);
            final String expected_zero_players = "There should be at least one player in a game";
            final String game_name = "CreateGameTest";
            final String numCoins = String.valueOf(5);
            final String radius = String.valueOf(25);
            final String duration = String.valueOf(5);

            Espresso.onView(withId(R.id.nameGameField)).perform(typeText(game_name), closeSoftKeyboard());
            Espresso.onView(withId(R.id.maxPlayerCountField)).perform(typeText(max_player_count_zero), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameNumCoins)).perform(typeText(numCoins), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameRadius)).perform(typeText(radius), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameDuration)).perform(typeText(duration), closeSoftKeyboard());

            Espresso.onView(withId(R.id.newGameSubmit)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.maxPlayerCountField)).check(matches(withError(expected_zero_players)));

            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Intents.release();
        }
    }


    @Test
    public void newGameZeroNumCoinsFieldError() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Intents.init();

            onView(ViewMatchers.withId(R.id.new_game)).perform(ViewActions.click());

            Thread.sleep(1000);

            final String max_player_count = String.valueOf(2);
            final String expected_zero_players = "There should be at least one coin in a game";
            final String game_name = "CreateGameTest";
            final String numCoins = String.valueOf(0);
            final String radius = String.valueOf(25);
            final String duration = String.valueOf(5);

            Espresso.onView(withId(R.id.nameGameField)).perform(typeText(game_name), closeSoftKeyboard());
            Espresso.onView(withId(R.id.maxPlayerCountField)).perform(typeText(max_player_count), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameNumCoins)).perform(typeText(numCoins), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameRadius)).perform(typeText(radius), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameDuration)).perform(typeText(duration), closeSoftKeyboard());

            Espresso.onView(withId(R.id.newGameSubmit)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.newGameNumCoins)).check(matches(withError(expected_zero_players)));


            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Intents.release();
        }
    }

    @Test
    public void newGameRadiusLessThanMinDistanceError() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Intents.init();

            onView(ViewMatchers.withId(R.id.new_game)).perform(ViewActions.click());

            Thread.sleep(1000);

            final String max_player_count = String.valueOf(2);
            final String expected_zero_players = "The radius of the game should be bigger than 5 meters";
            final String game_name = "CreateGameTest";
            final String numCoins = String.valueOf(5);
            final String radius = String.valueOf(1);
            final String duration = String.valueOf(5);

            Espresso.onView(withId(R.id.nameGameField)).perform(typeText(game_name), closeSoftKeyboard());
            Espresso.onView(withId(R.id.maxPlayerCountField)).perform(typeText(max_player_count), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameNumCoins)).perform(typeText(numCoins), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameRadius)).perform(typeText(radius), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameDuration)).perform(typeText(duration), closeSoftKeyboard());

            Espresso.onView(withId(R.id.newGameSubmit)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.newGameRadius)).check(matches(withError(expected_zero_players)));


            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Intents.release();
        }
    }

    @Test
    public void newGameZeroDurationFieldError() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Intents.init();

            onView(ViewMatchers.withId(R.id.new_game)).perform(ViewActions.click());

            Thread.sleep(1000);

            final String max_player_count = String.valueOf(2);
            final String expected_zero_players = "The game should last for more than 0 minute";
            final String game_name = "CreateGameTest";
            final String numCoins = String.valueOf(5);
            final String radius = String.valueOf(25);
            final String duration = String.valueOf(0);

            Espresso.onView(withId(R.id.nameGameField)).perform(typeText(game_name), closeSoftKeyboard());
            Espresso.onView(withId(R.id.maxPlayerCountField)).perform(typeText(max_player_count), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameNumCoins)).perform(typeText(numCoins), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameRadius)).perform(typeText(radius), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameDuration)).perform(typeText(duration), closeSoftKeyboard());

            Espresso.onView(withId(R.id.newGameSubmit)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.newGameDuration)).check(matches(withError(expected_zero_players)));


            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Intents.release();
        }
    }

    @Test
    public void loadWeatherWorks() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            scenario.onActivity(a -> {
                android.location.Location location = new android.location.Location(LocationManager.PASSIVE_PROVIDER);
                location.setLatitude(40.741895);
                location.setLongitude(-73.989308);
                a.loadWeather(location);

            });

            Thread.sleep(5000);

            scenario.onActivity(a -> {
                assertNotNull(a.getCurrentForecast());
                assertNotNull(a.getCurrentLocation());

            });


        } catch (@NonNull IllegalArgumentException | InterruptedException e) {
            assertEquals(1, 2);
            e.printStackTrace();
        }
    }

    @Test
    public void weatherTypeAndTemperatureAreNotEmpty(){
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            scenario.onActivity(a -> {
                android.location.Location location = new android.location.Location(LocationManager.PASSIVE_PROVIDER);
                location.setLatitude(0.7126);
                location.setLongitude(38.2699);
                a.loadWeather(location);
                a.setWeatherFieldsToday(a.getCurrentForecast().getWeatherReport(WeatherForecast.Day.TODAY));
            });
            Thread.sleep(5000);
            onView(withId(R.id.weather_temp_average)).check(matches(not(withText(""))));
            onView(withId(R.id.weather_type)).check(matches(not(withText(""))));
            onView(withId(R.id.weather_icon)).check(matches(not(withContentDescription("coin placeholder"))));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void returnButtonDoesNotMakeAppCrash() {
        // Since we disable the back button by simply returning the function shouldn't do anything
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            scenario.onActivity(a -> a.onBackPressed());
        }catch (Exception e){
            fail();
        }
    }

    @Test
    public void onlyNearGamesShow(){
        Game nearGame = addFirstGameToDatabase();
        Game farGame = addSecondGameToDatabase();

        try(ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Thread.sleep(5000);

            // Mock location
            scenario.onActivity(a -> {
                AndroidLocationService newLocationService = a.getLocationService();
                newLocationService.setMockedLocation(new LocationRepresentation(getMockedLocation()));
                a.setLocationService(newLocationService);
            });

            Thread.sleep(3000);

            onView(ViewMatchers.withId(R.id.join_game)).perform(ViewActions.click());

            Thread.sleep(7000);

            onView(ViewMatchers.withTagValue(Matchers.is(JoinGameImplementation.TAG_GAME_PREFIX + "777"))).check(matches(isDisplayed()));
            onView(ViewMatchers.withTagValue(Matchers.is(JoinGameImplementation.TAG_GAME_PREFIX + "888"))).check(doesNotExist());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}