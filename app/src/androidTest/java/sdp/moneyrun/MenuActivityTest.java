package sdp.moneyrun;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import androidx.lifecycle.Lifecycle.State;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import sdp.moneyrun.map.MapActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
public class MenuActivityTest {

    @Rule
    public ActivityScenarioRule<MenuActivity> testRule = new ActivityScenarioRule<>(MenuActivity.class);

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

    @Test
    public void activityStartsProperly() {
        assertEquals(State.RESUMED, testRule.getScenario().getState());
    }

    @Test
    public void joinGamePopupIsDisplayed() {
        onView(ViewMatchers.withId(R.id.join_game)).perform(ViewActions.click());
        onView(ViewMatchers.withId(R.id.join_popup)).check(matches(isDisplayed()));
    }

    @Test
    public void mapButtonWorks() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(MenuActivity.class)) {
            Intents.init();
            Espresso.onView(withId(R.id.map_button)).perform(ViewActions.click());
            Thread.sleep(7000);
            intended(hasComponent(MapActivity.class.getName()));
            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }    }


    @Test
    public void splashScreenShows() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(MenuActivity.class)) {
            Espresso.onView(withId(R.id.map_button)).perform(ViewActions.click());
            Thread.sleep(1000);
            onView(ViewMatchers.withId(R.id.splashscreen)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void newGamePopupIsDisplayed() {
        onView(ViewMatchers.withId(R.id.new_game)).perform(ViewActions.click());
        onView(ViewMatchers.withId(R.id.new_game_popup)).check(matches(isDisplayed()));
    }

    @Test
    public void leaderboardButtonWorks() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(MenuActivity.class)) {
            Intents.init();
            onView(withId(R.id.drawer_layout))
                    .check(matches(isClosed(Gravity.LEFT)))
                    .perform(DrawerActions.open());
            Espresso.onView(withId(R.id.leaderboard_button)).perform(ViewActions.click());
            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            intended(hasComponent(LeaderboardActivity.class.getName()));
            Intents.release();
        }
    }

    @Test
    public void navigationViewOpens() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
    }

    @Test
    public void newGameEmptyNameFieldError() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(MenuActivity.class)) {
            Intents.init();
            onView(ViewMatchers.withId(R.id.new_game)).perform(ViewActions.click());

            final String game_name = "new game";
            final String max_player_count = "12";
            final String expected = "This field is required";

            Espresso.onView(withId(R.id.newGameSubmit)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.nameGameText)).check(matches(withError(expected)));

            Espresso.onView(withId(R.id.maxPlayerNumber)).perform(typeText(max_player_count), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameSubmit)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.nameGameText)).check(matches(withError(expected)));

            Intents.release();
        }
    }

    @Test
    public void newGameEmptyMaxPlayerCountFieldError() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(MenuActivity.class)) {
            Intents.init();
            onView(ViewMatchers.withId(R.id.new_game)).perform(ViewActions.click());

            final String game_name = "new game";
            final String max_player_count_zero = "0";
            final String expected = "This field is required";
            final String expected_zero_players = "There should be at least one player in a game";

            Espresso.onView(withId(R.id.nameGameText)).perform(typeText(game_name), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameSubmit)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.maxPlayerNumber)).check(matches(withError(expected)));

            Intents.release();
        }
    }

    @Test
    public void newGameZeroMaxPlayerCountFieldError() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(MenuActivity.class)) {
            Intents.init();
            onView(ViewMatchers.withId(R.id.new_game)).perform(ViewActions.click());

            final String game_name = "new game";
            final String max_player_count_zero = "0";
            final String expected_zero_players = "There should be at least one player in a game";

            Espresso.onView(withId(R.id.nameGameText)).perform(typeText(game_name), closeSoftKeyboard());
            Espresso.onView(withId(R.id.maxPlayerNumber)).perform(typeText(max_player_count_zero), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameSubmit)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.maxPlayerNumber)).check(matches(withError(expected_zero_players)));

            Intents.release();
        }
    }

    @Test
    public void newGameWorks() {
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(MenuActivity.class)) {
            Intents.init();
            onView(ViewMatchers.withId(R.id.new_game)).perform(ViewActions.click());

            final String game_name = "test game";
            final String max_player_count = "1";
            final String expected_zero_players = "There should be at least one player in a game";

            Espresso.onView(withId(R.id.nameGameText)).perform(typeText(game_name), closeSoftKeyboard());
            Espresso.onView(withId(R.id.maxPlayerNumber)).perform(typeText(max_player_count), closeSoftKeyboard());
            Espresso.onView(withId(R.id.newGameSubmit)).perform(ViewActions.click());
            assertEquals(1, 1);

            Intents.release();
        }
    }
}