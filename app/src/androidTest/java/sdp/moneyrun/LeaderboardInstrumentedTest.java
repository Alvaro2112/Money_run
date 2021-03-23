package sdp.moneyrun;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LeaderboardInstrumentedTest {

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("sdp.moneyrun", appContext.getPackageName());
    }

    @Test
    public void addPlayerWorks(){
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a ->{
                Player player = new Player(123);
                player.setName("Alvaro Caudete");
                player.setScore(8008);
                a.addPlayer(player);
                assertEquals(a.getPlayerList().size(),1);
            });
        }

    }
}
