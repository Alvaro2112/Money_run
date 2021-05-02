package sdp.moneyrun.map;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;

import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.nio.charset.Charset;

import sdp.moneyrun.R;
import sdp.moneyrun.ui.map.OfflineMapDownloaderActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

public class OfflineMapDownloaderTest {

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

    @Test
    public void loadBarIsDisplayedTest() {

        try (ActivityScenario<OfflineMapDownloaderActivity> scenario = ActivityScenario.launch(OfflineMapDownloaderActivity.class)) {
            scenario.onActivity(activity -> {
                if (!activity.getIsEndNotified()) {
                    onView(withId(R.id.progress_bar_map_downloader)).check(matches(not(isDisplayed())));

                }
                else{
                    onView(withId(R.id.progress_bar_map_downloader)).check(matches(not(isDisplayed())));
                }

            });

        }
    }

    @Test
    public void downloadsMap() {

        try (ActivityScenario<OfflineMapDownloaderActivity> scenario = ActivityScenario.launch(OfflineMapDownloaderActivity.class)) {

            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            scenario.onActivity(activity -> {
                OfflineManager offlineManager = OfflineManager.getInstance(activity.getApplicationContext());
                offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback(){
                    @Override
                    public void onList(OfflineRegion[] offlineRegions) {
                        assertEquals(offlineRegions.length > 0,true);
                        String name;
                        try {
                            name = new JSONObject(new String(offlineRegions[0].getMetadata(), Charset.forName(OfflineMapDownloaderActivity.JSON_CHARSET)))
                                    .getString(OfflineMapDownloaderActivity.JSON_FIELD_REGION_NAME);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            name = " ";
                        }
                        assertEquals(name , "offline map");
                    }
                    @Override
                    public void onError(String error) {

                    }
                }
            );

        });
    }

    }
}
