package sdp.moneyrun;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class LeaderboardInstrumentedTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

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
        catch (Exception e){
            assertEquals(2,1);
        }

    }

    @Test
    public void addPlayerNullThrowsException(){
        exception.expect(RuntimeException.class);
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a ->{
                a.addPlayer(null);
            });
        }

    }

    @Test
    public void AddPlayerListWorks(){
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a ->{
                Player player = new Player(123);
                player.setName("Alvaro Caudete");
                player.setScore(8008);
                ArrayList<Player> list = new ArrayList<>();
                list.add(player);
                a.addPlayerList(list);
                assertEquals(a.getPlayerList().size(),1);
            });
        }
        catch (Exception e){
            assertEquals(2,1);
        }
    }

    @Test
    public void addPlayerListThrowsNullException(){
        exception.expect(RuntimeException.class);
        try (ActivityScenario<LeaderboardActivity> scenario = ActivityScenario.launch(LeaderboardActivity.class)) {
            scenario.onActivity(a ->{
                a.addPlayerList(null);
            });
        }
    }

}
