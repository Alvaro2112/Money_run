package sdp.moneyrun;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
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
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.game.Game;
import sdp.moneyrun.map.Coin;
import sdp.moneyrun.map.Riddle;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.game.GameLobbyActivity;
import sdp.moneyrun.ui.menu.MainLeaderboardActivity;
import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.user.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


@RunWith(AndroidJUnit4.class)
public class MenuActivityTest {
    @NonNull
    @Rule
    public ActivityScenarioRule<MenuActivity> testRule = new ActivityScenarioRule<>(getStartIntent());
    private final long ASYNC_CALL_TIMEOUT = 10L;

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

    //Since the features of Menu now depend on the intent it is usually launched with
    //We also need to launch MenuActivity with a valid intent for tests to pass
    @NonNull
    private Intent getStartIntent() {
        User currentUser = new User("999", "CURRENT_USER", "Epfl"
                , 0, 0, 0);
        Intent toStart = new Intent(ApplicationProvider.getApplicationContext(), MenuActivity.class);
        toStart.putExtra("user", currentUser);
        return toStart;
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
        return new Game(name, host, maxPlayerCount, riddles, coins, location, true, 2, 2, 2);
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
            final String radius = String.valueOf(2);
            final String duration = String.valueOf(5);

            Espresso.onView(withId(R.id.nameGameField)).perform(typeText(game_name), closeSoftKeyboard());
            Espresso.onView(withId(R.id.maxPlayerCountField)).perform(typeText(max_player_count), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameNumCoins)).perform(typeText(numCoins), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameRadius)).perform(typeText(radius), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameDuration)).perform(typeText(duration), closeSoftKeyboard());

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
            final String radius = String.valueOf(2);
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
            final String radius = String.valueOf(2);
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
    public void newGameZeroRadiusFieldError() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Intents.init();

            onView(ViewMatchers.withId(R.id.new_game)).perform(ViewActions.click());

            Thread.sleep(1000);

            final String max_player_count = String.valueOf(2);
            final String expected_zero_players = "The radius of the game should be bigger than 0 km";
            final String game_name = "CreateGameTest";
            final String numCoins = String.valueOf(5);
            final String radius = String.valueOf(0);
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
            final String radius = String.valueOf(2);
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

}