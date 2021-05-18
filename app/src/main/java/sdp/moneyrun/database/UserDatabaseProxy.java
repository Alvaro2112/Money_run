package sdp.moneyrun.database;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sdp.moneyrun.user.User;

@SuppressWarnings("FieldCanBeLocal")
public class UserDatabaseProxy extends DatabaseProxy {

    private final String TAG = UserDatabaseProxy.class.getSimpleName();

    private final String DATABASE_USER = "users";
    private final String DATABASE_USER_SCORE = "score";

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

        task.addOnCompleteListener(task1 -> {
            if (!task1.isSuccessful()) {
                Log.e(TAG, "Error getting data", task1.getException());

            } else {
                Log.d(TAG, String.valueOf(task1.getResult().getValue()));
            }
        });

        return task;
    }

    /**
     * Get all the users in the database.
     * @return Task containing the users datas
     */
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
        if (listener == null || user == null) {
            throw new IllegalArgumentException();
        }
        usersRef.child(String.valueOf(user.getUserId())).addValueEventListener(listener);
    }


    /**
     * Removes a ValueEventListener from a user entry in the db
     *
     * @param user
     * @param listener
     * @throws IllegalArgumentException on null listener or null user
     */
    public void removeUserListener(@Nullable User user, @Nullable ValueEventListener listener) {
        if (listener == null || user == null) {
            throw new IllegalArgumentException();
        }
        usersRef.child(String.valueOf(user.getUserId())).removeEventListener(listener);
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
                .limitToLast(n)
                .get();
    }

    public List<User> getUserListFromTaskFromSimilarName(Task<DataSnapshot> task, String filter) {
        if (filter == null) {
            throw new IllegalArgumentException("name should not be null.");
        }
        String cleanFilter = filter.trim().toLowerCase(Locale.getDefault());
        if (cleanFilter.equals("")) {
            throw new IllegalArgumentException("name should not be the empty string.");
        }

        if (!task.isComplete()) {
            return null;
        }
        if(!task.isSuccessful()){
            Log.e(TAG, "Error getting data", task.getException());
            return null;
        }

        DataSnapshot result = task.getResult();
        if (result == null) {
            return null;
        }

        ArrayList<User> resultList = new ArrayList<>();
        for(DataSnapshot dataSnapshot: result.getChildren()){
            User user = dataSnapshot.getValue(User.class);
            if(user == null || user.getName() == null){
                continue;
            }

            String cleanUserName = user.getName().trim().toLowerCase();
            if(cleanUserName.contains(cleanFilter)){
                resultList.add(user);
            }
        }

        return resultList;
    }
}
