package sdp.moneyrun;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.lifecycle.Lifecycle.State;

@RunWith(AndroidJUnit4.class)
public class MenuActivityTest extends TestCase {

    @Rule
    public ActivityScenarioRule<MenuActivity> testRule = new ActivityScenarioRule<>(MenuActivity.class);

    @Test
    public void activityStartsProperly() {
        assertEquals(State.CREATED, testRule.getScenario().getState());
        
    }


}