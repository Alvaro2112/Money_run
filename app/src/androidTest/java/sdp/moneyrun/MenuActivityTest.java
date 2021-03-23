package sdp.moneyrun;

import androidx.lifecycle.Lifecycle.State;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MenuActivityTest {

    @Rule
    public ActivityScenarioRule<MenuActivity> testRule = new ActivityScenarioRule<>(MenuActivity.class);

    @Test
    public void activityStartsProperly() {
        assertEquals(State.RESUMED, testRule.getScenario().getState());

    }

    @Test
    public void joinGamePopupIsDisplayed() {
        Intents.init();
        onView(ViewMatchers.withId(R.id.join_game)).perform(ViewActions.click());
        onView(ViewMatchers.withId(R.id.popup)).check(matches(isDisplayed()));
        Intents.release();
    }

    @Test
    public void askQuestionPopupIsDisplayed() {
        Intents.init();
        onView(ViewMatchers.withId(R.id.ask_question)).perform(ViewActions.click());
        onView(ViewMatchers.withId(R.id.question_popup)).check(matches(isDisplayed()));
        Intents.release();
    }

    @Test
    public void askQuestionPopupClosesWhenFirstAnswerClicked() {
        Intents.init();
        onView(ViewMatchers.withId(R.id.ask_question)).perform(ViewActions.click());
        onView(ViewMatchers.withId(R.id.question_choice_1)).perform(ViewActions.click());
        onView(ViewMatchers.withId(R.id.question_popup)).check(doesNotExist());


        Intents.release();
    }


}