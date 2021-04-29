package sdp.moneyrun.map;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;

import org.junit.Test;

import sdp.moneyrun.R;
import sdp.moneyrun.ui.map.OfflineMapDownloaderActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

public class OfflineMapDownloaderInstrumentedTest {

    @Test
    public void DownloadsAfterStartingActivity() {
        try (ActivityScenario<OfflineMapDownloaderActivity> scenario = ActivityScenario.launch(OfflineMapDownloaderActivity.class)) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a -> {
                assertEquals(a.getHasStartedDownload(),true);
            });
        }
    }

    @Test
    public void closeButtonWorks() {

        try (ActivityScenario<OfflineMapDownloaderActivity> scenario = ActivityScenario.launch(OfflineMapDownloaderActivity.class)) {
            Intents.init();
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            onView(withId(R.id.downloader_exit)).perform(ViewActions.click());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assertEquals(Lifecycle.State.DESTROYED, scenario.getState());

            Intents.release();
        }
    }


}
