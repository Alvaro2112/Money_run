package sdp.moneyrun.map;

import android.content.Context;
import android.location.Location;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import sdp.moneyrun.R;
import sdp.moneyrun.game.Game;
import sdp.moneyrun.ui.game.EndGameActivity;
import sdp.moneyrun.ui.map.MapActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MapInstrumentedTest {
private CountDownLatch moved =null;
    double minZoomForBuilding = 15.;


    @Test
    public void moveCameraToWorks() {
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {

            float lat = 8f;
            float lon = 8f;
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a ->{
                a.moveCameraTo(lat,lon);
            });
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a ->{
                LatLng latLng = a.getMapboxMap().getCameraPosition().target;
                assertEquals(latLng.getLatitude(), 8.0,0.1);
                assertEquals(latLng.getLongitude(), 8.0,0.1);
                System.out.println("LONGITUDE IS "+String.valueOf(latLng.getLatitude()));
            });
        }
    }

    @Test
    public void testSymbolManager() {
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a->{
               assertEquals( a.getSymbolManager().getIconAllowOverlap(),true);
                assertEquals( a.getSymbolManager().getTextAllowOverlap(),true);
            });
        }
        catch (Exception e){
            assertEquals(-1,2);
            e.printStackTrace();
        }
    }

    @Test
    public void locationTracking() {
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            scenario.onActivity(a->{
                assertEquals(a.getMapboxMap().getLocationComponent().getCameraMode(), CameraMode.TRACKING) ;
                assertEquals(a.getMapboxMap().getLocationComponent().getRenderMode(), RenderMode.COMPASS);
            });
        }
        catch (Exception e){
            assertEquals(-1,2);
            e.printStackTrace();
        }
    }




    @Test
    public void chronometerTest() {
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            scenario.onActivity(a->{
                assertFalse(a.getChronometer().isCountDown());
                assertTrue(a.getChronometer().getText().toString().contains("REMAINING TIME"));
            });
        }
        catch (Exception e){
            assertEquals(-1,2);
            e.printStackTrace();
        }
    }

    @Test
    public void onExplanationNeededWorks() {
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ArrayList<String> reasons = new ArrayList<>();
            reasons.add("e");
            scenario.onActivity(a->{
                a.onExplanationNeeded(reasons);
            });
            assertEquals(1,1);
        }
        catch (Exception e){
            assertEquals(-1,2);
            e.printStackTrace();
        }
    }

    @Test
    public void onPermissionResultWorks() {
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean granted = true;
            scenario.onActivity(a->{
                a.onPermissionResult(granted);
            });
            assertEquals(1,1);
        }
        catch (Exception e){
            assertEquals(-1,2);
            e.printStackTrace();
        }
    }

    @Test
    public void addCoinAddsCoinToMap() {
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a->{
                Location curloc = a.getCurrentLocation();
                Coin coin = new Coin(curloc.getLatitude()/2,curloc.getLongitude()/2,1);
                a.addCoin(coin);
                Coin coin2 = new Coin(curloc.getLatitude()/3,curloc.getLongitude()/100,1);
                a.addCoin(coin2);
            });
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a->{
                assertEquals(2,a.getSymbolManager().getAnnotations().size());
            });
        }
        catch (Exception e){
            assertEquals(-1,2);
            e.printStackTrace();
        }
    }


    @Test
    public void endGameStartsActivity() {
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            Intents.init();
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a -> {
                Game.endGame(a.getCollectedCoins(), a.getPlayerId(), a);
            });
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            intended(hasComponent(EndGameActivity.class.getName()));
            Intents.release();
        }
    }

    @Test
    public void questionButtonWorks() {


        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            onView(withId(R.id.new_question)).perform(ViewActions.click());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            onView(withId(R.id.ask_question_popup)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void questionWorksOnCorrectAnswer() {

        String question = "What is the color of the sky";
        String correctAnswer = "blue";
        Riddle riddle = new Riddle(question, correctAnswer, "blue", "green", "yellow", "brown");

        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {

            scenario.onActivity(a -> {
                a.onButtonShowQuestionPopupWindowClick(a.findViewById(R.id.mapView), true, R.layout.question_popup, riddle, null);
            });

            onView(withId(R.id.question_choice_1)).perform(ViewActions.click());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            onView(withId(R.id.correct_answer_popup)).check(matches(isDisplayed()));
            onView(withId(R.id.collect_coin)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void questionWorksOnWrongAnswer() {

        String question = "What is the color of the sky";
        String correctAnswer = "blue";
        Riddle riddle = new Riddle(question, correctAnswer, "blue", "green", "yellow", "brown");

        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {

            scenario.onActivity(a -> {
                a.onButtonShowQuestionPopupWindowClick(a.findViewById(R.id.mapView), true, R.layout.question_popup, riddle, null);
            });

            onView(withId(R.id.question_choice_2)).perform(ViewActions.click());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            onView(withId(R.id.wrong_answer_popup)).check(matches(isDisplayed()));
            onView(withId(R.id.continue_run)).check(matches(isDisplayed()));

        }

    }


    @Test(expected = NoMatchingViewException.class)
    public void continueRunButtonWorks() {

        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {

            String question = "What is the color of the sky";
            String correctAnswer = "blue";

            Riddle riddle = new Riddle(question, correctAnswer, "blue", "green", "yellow", "brown");

            scenario.onActivity(a -> {
                a.onButtonShowQuestionPopupWindowClick(a.findViewById(R.id.mapView), true, R.layout.question_popup, riddle, null);
            });

            onView(withId(R.id.question_choice_2)).perform(ViewActions.click());
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            onView(withId(R.id.continue_run)).perform(ViewActions.click());
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            onView(withId(R.id.ask_question_popup)).check(matches(not(isDisplayed())));


        }
    }

    @Test(expected = NoMatchingViewException.class)
    public void collectCoinButtonWorks() {

        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {

            String question = "What is the color of the sky";
            String correctAnswer = "blue";

            Riddle riddle = new Riddle(question, correctAnswer, "blue", "green", "yellow", "brown");

            scenario.onActivity(a -> {
                a.onButtonShowQuestionPopupWindowClick(a.findViewById(R.id.mapView), true, R.layout.question_popup, riddle, null);
            });

            onView(withId(R.id.question_choice_1)).perform(ViewActions.click());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            onView(withId(R.id.collect_coin)).perform(ViewActions.click());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            onView(withId(R.id.ask_question_popup)).check(matches(not(isDisplayed())));

        }
    }


    @Test
    public void showScoreWorks() {
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String default_text = "Score: 0";
            Espresso.onView(withId(R.id.map_score_view)).check(matches(withText(default_text)));

            scenario.onActivity(a->{
                Location curloc = a.getCurrentLocation();
                Coin coin = new Coin(curloc.getLatitude(),curloc.getLongitude(),1);
                a.addCoin(coin);
                a.removeCoin(coin, false);

            });
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String updated_text = "Score: 1";
            Espresso.onView(withId(R.id.map_score_view)).check(matches(withText(updated_text)));

        }
    }


    @Test
    public void collectCoinButtonCollectsCoin() {

        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {

            String question = "What is the color of the sky";
            String correctAnswer = "blue";

            Riddle riddle = new Riddle(question, correctAnswer, "blue", "green", "yellow", "brown");

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            scenario.onActivity(a -> {
                Location curloc = a.getCurrentLocation();
                Coin coin = new Coin(curloc.getLatitude() / 2,curloc.getLongitude(),1);

                a.onButtonShowQuestionPopupWindowClick(a.findViewById(R.id.mapView), true, R.layout.question_popup, riddle, coin);
            });

            onView(withId(R.id.question_choice_1)).perform(ViewActions.click());
            onView(withId(R.id.collect_coin)).perform(ViewActions.click());

            scenario.onActivity(a->{
                assertEquals(0,a.getSymbolManager().getAnnotations().size());
            });

        }
    }

    @Test
    public void closeButtonWorks() {

        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            Intents.init();

            onView(withId(R.id.close_map)).perform(ViewActions.click());
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
    public void LakeLemanIsDetectedAsInappropriateTest(){
        double lat =46.49396808615545;
        double lon =   6.638823143919147;
        Location location = new Location("");
        location.setLatitude(lat);
        location.setLongitude(lon);
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a-> {
                a.moveCameraWithoutAnimation(lat, lon, minZoomForBuilding);
            });
            try{
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a ->{
                assert(!a.isLocationAppropriate(location));
            });
        }
    }

    @Test
    public void SwissPlasmaCenterIsDetectedAsInappropriateTest(){
        double lat =46.517583898897826;
        double lon =    6.565050387400619;
        Location location = new Location("");
        location.setLatitude(lat);
        location.setLongitude(lon);
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a-> {
                a.moveCameraWithoutAnimation( lat,  lon, minZoomForBuilding);
            });
            try{
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a ->{
                assert(!a.isLocationAppropriate(location));
            });
        }
    }


    @Test
    public void RandomBuildingIsDetectedAsInappropriateTest(){
        double lat =46.517396499876476;
        double lon =     6.645705058098468;
        Location location = new Location("");
        location.setLatitude(lat);
        location.setLongitude(lon);
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a-> {
                a.moveCameraWithoutAnimation(lat, lon, minZoomForBuilding);
            });
            try{
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a ->{
                assert(!a.isLocationAppropriate(location));
            });
        }
    }

    @Test
    public void RandomParkIsDetectedAsAppropriateTest(){
        double minZoomForBuilding = 16.;
        double lat =46.51479170858094;
        double lon =    6.621513963216489;
        Location location = new Location("");
        location.setLatitude(lat);
        location.setLongitude(lon);
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a-> {
                a.moveCameraWithoutAnimation(lat, lon, minZoomForBuilding);
            });
            try{
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a ->{
                a.isLocationAppropriate(location);
                assert(a.isLocationAppropriate(location));
                //  System.out.println("At spc appropriate returns " + a.isLocationAppropriate(location));
            });
            try{
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void RandomFreePlaceIsDetectedAsAppropriateTest(){
        double lat =46.51192799046872;
        double lon =     6.619113183264966;
        Location location = new Location("");
        location.setLatitude(lat);
        location.setLongitude(lon);
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a-> {
                a.moveCameraWithoutAnimation(lat, lon, minZoomForBuilding);
            });
            try{
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a ->{
                    a.isLocationAppropriate(location);
               assert(a.isLocationAppropriate(location));
            });
        }
    }

    @Test
    public void sportCenterIsDetectedAsInappropriateTest(){
        double lat =46.511488;
        double lon =6.618642;
        Location location = new Location("");
        location.setLatitude(lat);
        location.setLongitude(lon);
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a-> {
                a.moveCameraWithoutAnimation(lat, lon, minZoomForBuilding);
            });
            try{
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a ->{
                a.isLocationAppropriate(location);
                assert(!a.isLocationAppropriate(location));
            });
        }
    }

    @Test
    public void RouteCantonaleIsDetectedAsInappropriateTest(){
        double lat =46.517319;
        double lon =6.568376;
        Location location = new Location("");
        location.setLatitude(lat);
        location.setLongitude(lon);
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a-> {
                a.moveCameraWithoutAnimation(lat, lon, minZoomForBuilding);
            });
            try{
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a ->{
                a.isLocationAppropriate(location);
                assert(!a.isLocationAppropriate(location));
            });
        }
    }

    @Test
    public void highwayIsDetectedAsInappropriateTest(){
        double lat =46.526493;
        double lon =6.580576;
        Location location = new Location("");
        location.setLatitude(lat);
        location.setLongitude(lon);
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a-> {
                a.moveCameraWithoutAnimation(lat, lon, minZoomForBuilding);
            });
            try{
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a ->{
                a.isLocationAppropriate(location);
                assert(!a.isLocationAppropriate(location));
            });
        }
    }

    @Test
    public void placingCoins() {
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a -> {
                a.moveCameraWithoutAnimation(a.getCurrentLocation().getLatitude(), a.getCurrentLocation().getLongitude(), minZoomForBuilding);

            });
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            scenario.onActivity(a -> {
                int numberOfCoins = 7;
                a.placeRandomCoins(numberOfCoins, 100);
                assertEquals(a.getRemainingCoins().size(), numberOfCoins);
            });
        }
    }




}
