package sdp.moneyrun.database;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sdp.moneyrun.Helpers;
import sdp.moneyrun.user.User;

@SuppressWarnings("FieldCanBeLocal")
public class UserDatabaseProxy extends DatabaseProxy {

    private final String TAG = UserDatabaseProxy.class.getSimpleName();

    private final String DATABASE_USER = "users";
    private final String DATABASE_USER_SCORE = "maxScoreInGame";

    @NonNull
    private final DatabaseReference usersRef;

    public UserDatabaseProxy() {
        super();

        usersRef = getReference().child(DATABASE_USER);
    }

    /**
     * Add a user to the database. If the user id already exists, erases previously kept data
     *
     * @param user the user to be put in the database
     */
    public void putUser(@Nullable User user) {
        if (user == null) {
            throw new IllegalArgumentException("user should not be null");
        }

        usersRef.child(String.valueOf(user.getUserId())).setValue(user);
    }

    /**
     * Remove a user to the database.
     *
     * @param user the user to be removed in the database
     */
    public void removeUser(@Nullable User user) {
        if (user == null) {
            throw new IllegalArgumentException("user should not be null");
        }

        usersRef.child(String.valueOf(user.getUserId())).removeValue();
    }

    /**
     * Get the Task (asynchronous !) from data base. The user instance can be retrieved -
     * once the task is completed - by using getUserFromTask
     *
     * @param userId
     * @return Task containing the user data
     */
    @NonNull
    public Task<DataSnapshot> getUserTask(@NonNull String userId) {
        Task<DataSnapshot> task = usersRef.child(userId).get();
        return Helpers.addOnCompleteListener(TAG, task);
    }

    /**
     * Get all the users in the database.
     * @return Task containing the users data
     */
    @NonNull
    public Task<DataSnapshot> getUsersTask(){
        return usersRef.get();
    }

    /** get a user from a task
    /**
     * get a user from a task
     *
     * @param task the task containing a user
     * @return the user inside the task or null if the task is not complete
     */
    @Nullable
    public User getUserFromTask(@NonNull Task<DataSnapshot> task) {
        if (task.isComplete()) {
            return task.getResult().getValue(User.class);
        } else {
            return null;
        }
    }

    /**
     * Will trigger an event each time the user is updated in the database
     * This means that the user should be added first
     *
     * @param user     the user who's database entry will be listened
     * @param listener the listener which describes what to do on change
     */
    public void addUserListener(@Nullable User user, @Nullable ValueEventListener listener) {
        Helpers.addOrRemoveListener(user, listener, usersRef, false);

    }


    /**
     * Removes a ValueEventListener from a user entry in the db
     *
     * @param user
     * @param listener
     * @throws IllegalArgumentException on null listener or null user
     */
    public void removeUserListener(@Nullable User user, @Nullable ValueEventListener listener) {
        Helpers.addOrRemoveListener(user, listener, usersRef, true);
    }

    /**
     * Returns the top users ordered by their score from the database.
     *
     * @param n the number of users to retrieve from the database
     * @return the task
     */
    @NonNull
    public Task<DataSnapshot> getLeaderboardUsers(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n should not be negative.");
        }

        return usersRef.orderByChild(DATABASE_USER_SCORE)
                .limitToLast(n).get();
    }

    /**
     * Get a list of players that have a name containing the filter string.
     * @param task the task from the database to sort
     * @param filter the name filter
     * @return a list of user whose names contain the filter string
     */
    @Nullable
    public List<User> getUserListFromTaskFromSimilarName(@NonNull Task<DataSnapshot> task, @NonNull String filter) {
        String cleanFilter = getCleanString(filter);

        if (!task.isComplete() || !task.isSuccessful()) {
            Log.e(TAG, "Error getting data", task.getException());
            return null;
        }

        DataSnapshot result = task.getResult();

        return getMatchingUserList(result, cleanFilter);
    }

    @NonNull
    private List<User> getMatchingUserList(@NonNull DataSnapshot result,
                                           @NonNull String cleanFilter){
        List<User> resultList = new ArrayList<>();
        for(DataSnapshot dataSnapshot: result.getChildren()){
            User user = dataSnapshot.getValue(User.class);
            if(user == null || user.getName() == null){
                continue;
            }

            addMatchingUser(resultList, user, cleanFilter);
        }
        return resultList;
    }

    /**
     * Get clean filter given name filter
     * @param string the string to clean
     * @return the cleaned string
     */
    @NonNull
    private String getCleanString(@NonNull String string){
        String cleanString = string.trim().toLowerCase(Locale.getDefault());
        if (cleanString.equals("")) {
            throw new IllegalArgumentException("name should not be the empty string.");
        }

        return cleanString;
    }

    /**
     * Add a player to the list if it is matching the name/filter requirements
     */
    private void addMatchingUser(@NonNull List<User> resultList,
                                 @NonNull User user,
                                 @NonNull String cleanFilter){
        String name = user.getName();
        if(name == null){
            throw new IllegalArgumentException("name should not be null.");
        }

        String cleanUserName = getCleanString(user.getName());
        if(cleanUserName.contains(cleanFilter)){
            resultList.add(user);
        }
    }

    /**
     * Given a user, updates their friend list given the database
     * @param user the user to update
     * @return the task that retrieves the user from the database
     */
    @Nullable
    public Task<DataSnapshot> updatedFriendListFromDatabase(@Nullable User user){
        if(user == null || user.getUserId() == null){
            return null;
        }
        UserDatabaseProxy db = new UserDatabaseProxy();
        Task<DataSnapshot> userTask = db.getUserTask(user.getUserId());
        userTask.addOnCompleteListener(task -> {
            User userUpdated = db.getUserFromTask(task);
            if(userUpdated != null){
                user.setFriendIdList(userUpdated.getFriendIdList());
            }
        });

        return userTask;
    }
}
