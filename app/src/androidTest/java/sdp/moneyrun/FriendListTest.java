package sdp.moneyrun;

import android.content.Intent;
import android.location.Location;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.database.FirebaseDatabase;

import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sdp.moneyrun.database.UserDatabaseProxy;
import sdp.moneyrun.database.game.GameDatabaseProxy;
import sdp.moneyrun.game.Game;
import sdp.moneyrun.game.GameBuilder;
import sdp.moneyrun.location.AndroidLocationService;
import sdp.moneyrun.location.LocationRepresentation;
import sdp.moneyrun.menu.friendlist.FriendListListAdapter;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.player.PlayerBuilder;
import sdp.moneyrun.ui.MainActivity;
import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.ui.menu.friendlist.AddFriendListActivity;
import sdp.moneyrun.ui.menu.friendlist.FriendListActivity;
import sdp.moneyrun.user.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class FriendListTest {

    private static String randomString;
    private static final List<User> usersDatabase = getUsers();

    @BeforeClass
    public static void buildDatabase() {
        if (!MainActivity.calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            MainActivity.calledAlready = true;
        }

        UserDatabaseProxy db = new UserDatabaseProxy();
        for (User user : usersDatabase) {
            db.putUser(user);
        }
    }

    @AfterClass
    public static void removeDatabase() {
        UserDatabaseProxy db = new UserDatabaseProxy();
        for (User user : usersDatabase) {
            db.removeUser(user);
        }
    }

    private static List<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();

        Random random = new Random();
        randomString = Integer.toString(random.nextInt(100000000));

        User currentUser = new User("hM667", "CURRENT_USER" + randomString, 0, 0, 0);
        ArrayList<String> friendIdList = new ArrayList<>();
        friendIdList.add("hM668");
        friendIdList.add("hM669");
        currentUser.setFriendIdList(friendIdList);

        User friend1 = new User("hM668", "Paul" + randomString, 0, 0, 0);
        User friend2 = new User("hM669", "Jacques" + randomString, 0, 0, 0);
        User user3 = new User("hM670", "Patricia" + randomString, 0, 0, 0);
        User user4 = new User("hM671", "Marc" + randomString, 0, 0, 0);
        User user5 = new User("hM672", "Marceline" + randomString, 0, 0, 0);

        users.add(currentUser);
        users.add(friend1);
        users.add(friend2);
        users.add(user3);
        users.add(user4);
        users.add(user5);

        return users;
    }

    private Intent getStartIntent2() {
        Intent toStart = new Intent(ApplicationProvider.getApplicationContext(), AddFriendListActivity.class);
        toStart.putExtra("user", usersDatabase.get(0));
        return toStart;
    }

    @Test
    public void backButtonDoesNothing() {
        try (ActivityScenario<AddFriendListActivity> scenario = ActivityScenario.launch(getStartIntent2())) {
            assertEquals(Lifecycle.State.RESUMED, scenario.getState());
            onView(isRoot()).perform(ViewActions.pressBack());
            assertEquals(Lifecycle.State.RESUMED, scenario.getState());
        }
    }

    @Test
    public void backButtonDoesNothing1() {
        try (ActivityScenario<FriendListActivity> scenario = ActivityScenario.launch(FriendListActivity.class)) {
            assertEquals(Lifecycle.State.RESUMED, scenario.getState());
            onView(isRoot()).perform(ViewActions.pressBack());
            assertEquals(Lifecycle.State.RESUMED, scenario.getState());
        }
    }

    private Intent getStartIntent() {
        Intent toStart = new Intent(ApplicationProvider.getApplicationContext(), FriendListActivity.class);
        toStart.putExtra("user", usersDatabase.get(0));
        return toStart;
    }

    private Location getMockedLocation() {
        Location gameLocation = new Location("");
        gameLocation.setLongitude(12.);
        gameLocation.setLatitude(12.);

        return gameLocation;
    }

    private Game addGameToDatabase() {
        // Define game location
        Location gameLocation = getMockedLocation();

        // Define game host
        User userHost = usersDatabase.get(1);
        PlayerBuilder hostBuilder = new PlayerBuilder();
        Player host = hostBuilder.setPlayerId(userHost.getUserId())
                .setName(userHost.getName())
                .setScore(0)
                .build();

        List<Player> players = new ArrayList<>();
        players.add(host);

        // Define game
        GameDatabaseProxy gdb = new GameDatabaseProxy();
        GameBuilder gb = new GameBuilder();
        Game game = gb.setName("Paul's game")
                .setHost(host)
                .setMaxPlayerCount(10)
                .setStartLocation(gameLocation)
                .setIsVisible(false)
                .setCoins(new ArrayList<>())
                .setPlayers(players)
                .setRiddles(new ArrayList<>())
                .setNumCoins(10)
                .setRadius(10)
                .setDuration(999999)
                .build();
        game.setId(host.getPlayerId());
        gdb.putGame(game);

        return game;
    }

    @Test
    public void defaultFriendsWork() {
        UserDatabaseProxy db = new UserDatabaseProxy();
        for (User user : usersDatabase) {
            db.putUser(user);
        }

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try (ActivityScenario<FriendListActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Thread.sleep(6000);
            //Check default friends
            onView(ViewMatchers.withTagValue(Matchers.is(usersDatabase.get(1).getUserId()))).check(matches(isDisplayed()));
            onView(ViewMatchers.withTagValue(Matchers.is(usersDatabase.get(2).getUserId()))).check(matches(isDisplayed()));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (User user : usersDatabase) {
            db.removeUser(user);
        }
    }

    @Test
    public void addFriendWorks() {
        UserDatabaseProxy db = new UserDatabaseProxy();
        for (User user : usersDatabase) {
            db.putUser(user);
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Thread.sleep(3000);
            //Join add friends
            onView(ViewMatchers.withId(R.id.friend_list_search_button)).perform(ViewActions.click());

            //Search
            onView(ViewMatchers.withId(R.id.friend_add_list_filter)).perform(typeText(randomString), closeSoftKeyboard());
            onView(ViewMatchers.withId(R.id.friend_add_list_search_button)).perform(ViewActions.click());

            Thread.sleep(3000);

            //Add Marceline to the friend list
            onView(ViewMatchers.withTagKey(R.string.add_friend_tag_1, Matchers.is(usersDatabase.get(5).getUserId()))).perform(ViewActions.click());

            //Check that Marceline is a new friend
            onView(ViewMatchers.withId(R.id.friend_add_list_button_back)).perform(ViewActions.click());

            Thread.sleep(3000);

            onView(ViewMatchers.withTagValue(Matchers.is(usersDatabase.get(1).getUserId()))).check(matches(isDisplayed()));
            onView(ViewMatchers.withTagValue(Matchers.is(usersDatabase.get(2).getUserId()))).check(matches(isDisplayed()));
            onView(ViewMatchers.withTagValue(Matchers.is(usersDatabase.get(5).getUserId()))).check(matches(isDisplayed()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (User user : usersDatabase) {
            db.removeUser(user);
        }
    }

    private Intent getStartIntent1() {
        User currentUser = new User("888", "CURRENT_USER"
                , 0, 0, 0);
        Intent toStart = new Intent(ApplicationProvider.getApplicationContext(), FriendListActivity.class);
        toStart.putExtra("user", currentUser);
        return toStart;
    }

    @Test
    public void goBackToMenuWorks() {

        try (ActivityScenario<FriendListActivity> scenario = ActivityScenario.launch(getStartIntent1())) {
            Intents.init();
            Thread.sleep(3000);
            //Join add friends
            //Search
            onView(ViewMatchers.withId(R.id.back_from_friend_list_button)).perform(ViewActions.click());

            Thread.sleep(3000);

            intended(hasComponent(MenuActivity.class.getName()));
            Intents.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
            Intents.release();
        }

    }


    @Test
    public void removeFriendWorks() {
        UserDatabaseProxy db = new UserDatabaseProxy();
        for (User user : usersDatabase) {
            db.putUser(user);
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try (ActivityScenario<FriendListActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Thread.sleep(3000);
            //Join add friends
            onView(ViewMatchers.withId(R.id.friend_list_search_button)).perform(ViewActions.click());

            //Search
            onView(ViewMatchers.withId(R.id.friend_add_list_filter)).perform(typeText(randomString), closeSoftKeyboard());
            onView(ViewMatchers.withId(R.id.friend_add_list_search_button)).perform(ViewActions.click());

            Thread.sleep(3000);

            //Remove Paul to the friend list
            onView(ViewMatchers.withTagKey(R.string.add_friend_tag_1, Matchers.is(usersDatabase.get(1).getUserId()))).perform(ViewActions.click());

            //Check that Paul is not a friend anymore
            onView(ViewMatchers.withId(R.id.friend_add_list_button_back)).perform(ViewActions.click());

            Thread.sleep(3000);

            onView(ViewMatchers.withTagValue(Matchers.is(usersDatabase.get(1).getUserId()))).check(doesNotExist());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (User user : usersDatabase) {
            db.removeUser(user);
        }
    }

    @Test
    public void JoinFriendGameWorks() {
        UserDatabaseProxy db = new UserDatabaseProxy();
        for (User user : usersDatabase) {
            db.putUser(user);
        }

        Game game = addGameToDatabase();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try (ActivityScenario<FriendListActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Thread.sleep(5000);

            // Mock location
            scenario.onActivity(a -> {
                AndroidLocationService newLocationService = a.getLocationService();
                newLocationService.setMockedLocation(new LocationRepresentation(getMockedLocation()));
                a.setLocationService(newLocationService);
            });

            Thread.sleep(3000);

            // Test join button states
            onView(ViewMatchers.withTagValue(Matchers.is(FriendListListAdapter.TAG_BUTTON_PREFIX + usersDatabase.get(1).getUserId()))).check(matches(isDisplayed()));
            onView(ViewMatchers.withTagValue(Matchers.is(FriendListListAdapter.TAG_BUTTON_PREFIX + usersDatabase.get(2).getUserId()))).check(matches(not(isDisplayed())));

            // Test join buttons functionality
            onView(ViewMatchers.withTagValue(Matchers.is(FriendListListAdapter.TAG_BUTTON_PREFIX + usersDatabase.get(1).getUserId()))).perform(ViewActions.click());
            onView(ViewMatchers.withId(R.id.lobby_title)).check(matches(withText(game.getName())));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (User user : usersDatabase) {
            db.removeUser(user);
        }
    }
}
