package sdp.moneyrun.map;

import androidx.test.core.app.ActivityScenario;

import org.junit.Test;

import sdp.moneyrun.ui.map.OfflineMapDownloaderActivity;

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

}
