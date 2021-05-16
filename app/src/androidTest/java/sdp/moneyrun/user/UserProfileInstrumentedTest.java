package sdp.moneyrun.user;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import sdp.moneyrun.R;
import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.ui.player.UserProfileActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class UserProfileInstrumentedTest {

    @NonNull
    @Rule
    public ActivityScenarioRule<UserProfileActivity> testRule = new ActivityScenarioRule<>(getStartIntent());
    @NonNull
    @Rule
    public ActivityScenarioRule<UserProfileActivity> testRuleProfile = new ActivityScenarioRule<>(getStartIntent());

    @NonNull
    private Intent getStartIntent() {
        User currentUser = new User("999", "CURRENT_USER", "Epfl"
                , 0, 0, 0);
        Intent toStart = new Intent(ApplicationProvider.getApplicationContext(), UserProfileActivity.class);
        toStart.putExtra("user", currentUser);
        return toStart;
    }

    @Test
    public void checkButtonOpenRightActivities() {
        User currentUser = new User("999", "CURRENT_USER", "Epfl"
                , 0, 0, 0);
        Intent toStart = new Intent(ApplicationProvider.getApplicationContext(), MenuActivity.class);
        toStart.putExtra("user", currentUser);
        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(toStart)) {
            Intents.init();
            onView(ViewMatchers.withId(R.id.drawer_layout))
                    .check(matches(isClosed(Gravity.LEFT)))
                    .perform(DrawerActions.open());
            Espresso.onView(ViewMatchers.withId(R.id.profile_button)).perform(ViewActions.click());
            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

            intended(hasComponent(UserProfileActivity.class.getName()));
            Intents.release();
        }
    }

    @Test
    public void checkProfileInfoDisplayedWhenPlayerExists() {
        //TODO: find a way to put info into result array in UserProfileActivity
        try (ActivityScenario<UserProfileActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Intents.init();

            String name = "John";
            String address = "New York";
            int diedN = 0;
            int playedN = 5;
            User user = new User("12345");
            user.setName(name);
            user.setAddress(address);
            user.setNumberOfDiedGames(diedN);
            user.setNumberOfPlayedGames(playedN);
            scenario.onActivity(a -> a.setDisplayedTexts(user));

            Espresso.onView(withId(R.id.playerDiedGames))
                    .check(matches(withText("User has died 0 many times")));
            Espresso.onView(withId(R.id.playerPlayedGames))
                    .check(matches(withText("User has played 5 many games")));
            Espresso.onView(withId(R.id.playerAddress))
                    .check(matches(withText("User address : New York")));
            Espresso.onView(withId(R.id.playerName))
                    .check(matches(withText("User name : John")));
            Intents.release();
        }
    }

    @Test
    public void checkNoInfoDisplayedWhenPlayerDoesNotExist() {

        try (ActivityScenario<UserProfileActivity> scenario = ActivityScenario.launch(UserProfileActivity.class)) {
            Intents.init();
            Espresso.onView(withId(R.id.playerEmptyMessage))
                    .perform(click())
                    .check(matches(withText("PLAYER IS EMPTY GO BACK TO MAIN MANY TO FILL UP THE INFO FOR THE PLAYER")));
            Intents.release();
        }
    }

    @Test
    public void buttonBackToMenuWorks() {

        try (ActivityScenario<UserProfileActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Intents.init();
            onView(withId(R.id.goBackToMainMenu)).perform(click());

            Thread.sleep(1000);

            intended(hasComponent(MenuActivity.class.getName()));
            Intents.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Intents.release();
        }
    }
}
