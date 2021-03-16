package sdp.moneyrun;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.widget.Button;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import sdp.moneyrun.permissions.PermissionsRequester;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PlayerProfileInstrumentedTest {

    @Rule
    public ActivityScenarioRule<MenuActivity> testRuleMenu = new ActivityScenarioRule<>(MenuActivity.class);

    @Rule
    public ActivityScenarioRule<PlayerProfileActivity> testRuleProfile = new ActivityScenarioRule<>(PlayerProfileActivity.class);


    @Test
    public void checkButtonOpenRightActivities() throws Throwable{
        try(ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(MenuActivity.class)) {
            Intents.init();
            Espresso.onView(ViewMatchers.withId(R.id.go_to_profile_button)).perform(ViewActions.click());
            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

            intended(hasComponent(PlayerProfileActivity.class.getName()));
            Intents.release();

        }
    }
    @Test
    public void checkProfileInfoDisplayedWhenPlayerExists(){
        //TODO: find a way to put info into result array in PlayerProfileActivity
        try(ActivityScenario<PlayerProfileActivity> scenario = ActivityScenario.launch(PlayerProfileActivity.class)) {
            Intents.init();
            String[] content = {"John", "New York", "0", "5"};
            scenario.onActivity(a->{
                PlayerProfileActivity playerProfileActivity = (PlayerProfileActivity)a;
                playerProfileActivity.setDisplayedTexts(content);
            });
            Espresso. onView(withId(R.id.playerDiedGames))
                    .check(matches(withText("Player has died 0 many times")));
            Espresso. onView(withId(R.id.playerPlayedGames))
                    .check(matches(withText("Player has played 5 many games")));
            Espresso. onView(withId(R.id.playerAddress))
                    .check(matches(withText("Player address : New York")));
            Espresso. onView(withId(R.id.playerName))
                    .check(matches(withText("Player name : John")));
            Intents.release();
        }
    }
    @Test
    public void checkNoInfoDisplayedWhenPlayerDoesNotExist(){
        try(ActivityScenario<PlayerProfileActivity> scenario = ActivityScenario.launch(PlayerProfileActivity.class)) {
            Intents.init();
            Espresso.onView(withId(R.id.playerEmptyMessage))
                    .perform(click())
                    .check(matches(withText("PLAYER IS EMPTY GO BACK TO MAIN MANY TO FILL UP THE INFO FOR THE PLAYER")));
            Intents.release();
        }
    }
}
