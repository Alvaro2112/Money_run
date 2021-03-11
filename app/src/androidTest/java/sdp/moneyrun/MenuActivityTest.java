package sdp.moneyrun;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static java.util.regex.Pattern.matches;

@RunWith(AndroidJUnit4.class)
public class MenuActivityTest extends TestCase {

    @Test
    public void testEvent() {
        ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(MenuActivity.class);
        //onView(withId(R.layout.activity_menu)).check(matches(isDisplayed()));
    }



}