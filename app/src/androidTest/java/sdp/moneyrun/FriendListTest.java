package sdp.moneyrun;

import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
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
import sdp.moneyrun.ui.MainActivity;
import sdp.moneyrun.ui.menu.FriendListActivity;
import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.user.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

@RunWith(AndroidJUnit4.class)
public class FriendListTest {

    private static final List<User> usersDatabase = getUsers();
    private static String randomString;

    @BeforeClass
    public static void buildDatabase(){
            if (!MainActivity.calledAlready) {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                MainActivity.calledAlready = true;
            }

        UserDatabaseProxy db = new UserDatabaseProxy();
        for(User user : usersDatabase){
            db.putUser(user);
        }
    }

    @AfterClass
    public static void removeDatabase(){
        UserDatabaseProxy db = new UserDatabaseProxy();
        for(User user : usersDatabase){
            db.removeUser(user);
        }
    }


    private static List<User> getUsers(){
        ArrayList<User> users = new ArrayList<>();

        Random random = new Random();
        randomString = Integer.toString(random.nextInt(100000000));

        User currentUser = new User("hM667", "CURRENT_USER" + randomString, "Epfl", 0, 0, 0);
        ArrayList<String> friendIdList = new ArrayList<>();
        friendIdList.add("hM668");
        friendIdList.add("hM669");
        currentUser.setFriendIdList(friendIdList);

        User friend1 = new User("hM668", "Paul" + randomString, "Lausanne", 0, 0, 0);
        User friend2 = new User("hM669", "Jacques" + randomString, "Lucens", 0, 0, 0);
        User user3 = new User("hM670", "Patricia" + randomString, "Paris", 0, 0, 0);
        User user4 = new User("hM671", "Marc" + randomString, "Berne", 0, 0, 0);
        User user5 = new User("hM672", "Marceline" + randomString, "Vers-chez-les-Blanc", 0, 0, 0);

        users.add(currentUser);
        users.add(friend1);
        users.add(friend2);
        users.add(user3);
        users.add(user4);
        users.add(user5);

        return users;
    }

    private Intent getStartIntent() {
        Intent toStart = new Intent(ApplicationProvider.getApplicationContext(), FriendListActivity.class);
        toStart.putExtra("user", usersDatabase.get(0));
        return toStart;
    }

    @Test
    public void defaultFriendsWork(){
        UserDatabaseProxy db = new UserDatabaseProxy();
        for(User user : usersDatabase){
            db.putUser(user);
        }

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Thread.sleep(6000);
            //Check default friends
            onView(ViewMatchers.withTagValue(Matchers.is(usersDatabase.get(1).getUserId()))).check(matches(isDisplayed()));
            onView(ViewMatchers.withTagValue(Matchers.is(usersDatabase.get(2).getUserId()))).check(matches(isDisplayed()));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(User user : usersDatabase){
            db.removeUser(user);
        }
    }

    @Test
    public void addFriendWorks(){
        UserDatabaseProxy db = new UserDatabaseProxy();
        for(User user : usersDatabase){
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

        for(User user : usersDatabase){
            db.removeUser(user);
        }
    }

    @Test
    public void removeFriendWorks() {
        UserDatabaseProxy db = new UserDatabaseProxy();
        for(User user : usersDatabase){
            db.putUser(user);
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try (ActivityScenario<MenuActivity> scenario = ActivityScenario.launch(getStartIntent())) {
            Thread.sleep(5000);
            //Join add friends
            onView(ViewMatchers.withId(R.id.friend_list_search_button)).perform(ViewActions.click());
            Thread.sleep(5000);
            //Search
            onView(ViewMatchers.withId(R.id.friend_add_list_filter)).perform(typeText(randomString), closeSoftKeyboard());
            onView(ViewMatchers.withId(R.id.friend_add_list_search_button)).perform(ViewActions.click());

            Thread.sleep(5000);

            //Remove Paul to the friend list
            onView(ViewMatchers.withTagKey(R.string.add_friend_tag_1, Matchers.is(usersDatabase.get(1).getUserId()))).perform(ViewActions.click());

            //Check that Paul is not a friend anymore
            onView(ViewMatchers.withId(R.id.friend_add_list_button_back)).perform(ViewActions.click());

            Thread.sleep(5000);

            onView(ViewMatchers.withTagValue(Matchers.is(usersDatabase.get(1).getUserId()))).check(doesNotExist());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(User user : usersDatabase){
            db.removeUser(user);
        }
    }
}
