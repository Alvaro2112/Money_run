package sdp.moneyrun.authentication;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.database.FirebaseDatabase;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import sdp.moneyrun.R;
import sdp.moneyrun.ui.MainActivity;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.menu.LeaderboardActivity;
import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.ui.player.PlayerProfileActivity;
import sdp.moneyrun.ui.authentication.RegisterPlayerActivity;

import static android.os.Trace.isEnabled;
import static android.service.autofill.Validators.not;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static sdp.moneyrun.authentication.SignUpActivityTest.withError;

@RunWith(AndroidJUnit4.class)
public class RegisterPlayerInstrumentedTest {
    @BeforeClass
    public static void setPersistence(){
        if(!MainActivity.calledAlready){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            MainActivity.calledAlready = true;
        }
    }

    @Test
    public void checkViewsAreUpdatedWhenCorrectTyping(){
        try(ActivityScenario<RegisterPlayerActivity> scenario = ActivityScenario.launch(RegisterPlayerActivity.class)){
            Intents.init();
            String name = "John";
            String address = "New York";
            String pet = "Dog";
            String color = "Green";
            Espresso.onView(ViewMatchers.withId(R.id.registerNameText)).perform(typeText(name), closeSoftKeyboard());
            Espresso.onView(withId(R.id.registerAddressText)).perform(typeText(address), closeSoftKeyboard());
            Espresso.onView(withId(R.id.registerAnimalText)).perform(typeText(pet), closeSoftKeyboard());
            Espresso.onView(withId(R.id.registerColorText)).perform(typeText(color), closeSoftKeyboard());
            Espresso.onView(withId(R.id.registerNameText)).check(matches(withText(name)));
            Espresso.onView(withId(R.id.registerAddressText)).check(matches(withText(address)));
            Espresso.onView(withId(R.id.registerAnimalText)).check(matches(withText(pet)));
            Espresso.onView(withId(R.id.registerColorText)).check(matches(withText(color)));
            Espresso.onView(withId(R.id.submitProfileButton)).perform(click());
            Intents.release();
        }catch (Exception e){
            e.printStackTrace();
            Intents.release();
        }
    }
    @Test
    public void checkErrorWhenNameIsEmpty() {
        try (ActivityScenario<RegisterPlayerActivity> scenario = ActivityScenario.launch(RegisterPlayerActivity.class)) {
            Intents.init();
            final String expected = "Name field is empty";
            Espresso.onView(withId(R.id.submitProfileButton)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.registerNameText)).check(matches(withError(expected)));
            Intents.release();
        }
    }
    @Test
    public void checkErrorWhenAddressIsEmpty(){
        try(ActivityScenario<RegisterPlayerActivity> scenario = ActivityScenario.launch(RegisterPlayerActivity.class)){
            Intents.init();
            final String expected = "Address field is empty";
            Espresso.onView(withId(R.id.submitProfileButton)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.registerAddressText)).check(matches(withError(expected)));
            Intents.release();
        }
    }
    @Test
    public void checkErrorWhenAnimalIsEmpty(){
        try(ActivityScenario<RegisterPlayerActivity> scenario = ActivityScenario.launch(RegisterPlayerActivity.class)){
            Intents.init();
            final String expected = "Animal field is empty";
            Espresso.onView(withId(R.id.submitProfileButton)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.registerAnimalText)).check(matches(withError(expected)));
            Intents.release();
        }
    }
    @Test
    public void checkErrorWhenColorIsEmpty(){
        try(ActivityScenario<RegisterPlayerActivity> scenario = ActivityScenario.launch(RegisterPlayerActivity.class)){
            Intents.init();
            final String expected = "Color field is empty";
            Espresso.onView(withId(R.id.submitProfileButton)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.registerColorText)).check(matches(withError(expected)));
            Intents.release();
        }
    }
    @Test
    public void checkProfileHasCorrectAttributesWhenPlayerRegisters(){
        try(ActivityScenario<RegisterPlayerActivity> scenario = ActivityScenario.launch(RegisterPlayerActivity.class)) {
            Intents.init();
            String name = "John";
            String address = "New York";
            String pet = "Dog";
            String color = "Green";
            Espresso.onView(withId(R.id.registerNameText)).perform(typeText(name), closeSoftKeyboard());
            Espresso.onView(withId(R.id.registerAddressText)).perform(typeText(address), closeSoftKeyboard());
            Espresso.onView(withId(R.id.registerAnimalText)).perform(typeText(pet), closeSoftKeyboard());
            Espresso.onView(withId(R.id.registerColorText)).perform(typeText(color), closeSoftKeyboard());
            Espresso.onView(withId(R.id.submitProfileButton)).perform(click());
            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Thread.sleep(1000);
            intended(hasComponent(MenuActivity.class.getName()));
            Espresso.onView(withId(R.id.profile_button)).perform(click());
            Context appContext2 = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Thread.sleep(1000);
            intended(hasComponent(PlayerProfileActivity.class.getName()));
            Espresso.onView(withId(R.id.playerDiedGames))
                    .check(matches(withText("Player has died 0 many times")));
            Espresso.onView(withId(R.id.playerPlayedGames))
                    .check(matches(withText("Player has played 0 many games")));
            Espresso.onView(withId(R.id.playerAddress))
                    .check(matches(withText("Player address : New York")));
            Espresso.onView(withId(R.id.playerName))
                    .check(matches(withText("Player name : John")));
            Intents.release();
        }catch (Exception e){
            e.printStackTrace();
            Intents.release();
        }
    }
    @Test
    public void guestModeDisablesJoinGameButtonInMenu(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RegisterPlayerActivity.class);
        intent.putExtra("guestPlayer", true);
        try(ActivityScenario<RegisterPlayerActivity> scenario = ActivityScenario.launch(intent)) {
            Intents.init();
            String name = "John";
            String address = "New York";
            String pet = "Dog";
            String color = "Green";
            Espresso.onView(withId(R.id.registerNameText)).perform(typeText(name), closeSoftKeyboard());
            Espresso.onView(withId(R.id.registerAddressText)).perform(typeText(address), closeSoftKeyboard());
            Espresso.onView(withId(R.id.registerAnimalText)).perform(typeText(pet), closeSoftKeyboard());
            Espresso.onView(withId(R.id.registerColorText)).perform(typeText(color), closeSoftKeyboard());
            Espresso.onView(withId(R.id.submitProfileButton)).perform(click());
            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            intended(hasComponent(MenuActivity.class.getName()));
        }
    }
}
