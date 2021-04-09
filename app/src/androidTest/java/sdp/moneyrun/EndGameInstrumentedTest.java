package sdp.moneyrun;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;

import org.junit.Test;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

public class EndGameInstrumentedTest {


    @Test
    public void updateTextFailsWithoutLists() {
        try (ActivityScenario<EndGameActivity> scenario = ActivityScenario.launch(EndGameActivity.class)) {

        Espresso.onView(withId(R.id.end_game_text)).check(matches(withText("Unfortunately the coin you collected have been lost")));
        }
        catch (Exception e){
                assertEquals(-1,2);
                e.printStackTrace();
            }
        }

    @Test
    public void updateTextDisplaysGoodNumbers() {
        try (ActivityScenario<EndGameActivity> scenario = ActivityScenario.launch(EndGameActivity.class)) {
            scenario.onActivity(a -> {
                a.updateText(1, 1,true);
            });

            StringBuilder textBuilder = new StringBuilder();
            textBuilder = textBuilder.append("You have gathered").append(1).append("coins");
            textBuilder = textBuilder.append("\n");
            textBuilder = textBuilder.append("For a total score of ").append(1);
            String text = textBuilder.toString();
            Espresso.onView(withId(R.id.end_game_text)).check(matches(withText(text)));
        }
        catch (Exception e){
            assertEquals(-1,2);
            e.printStackTrace();
        }
    }


}
