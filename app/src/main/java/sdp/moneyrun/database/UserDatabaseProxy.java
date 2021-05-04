package sdp.moneyrun.database;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import sdp.moneyrun.user.User;

public class UserDatabaseProxy extends DatabaseProxy {

    private final String TAG = UserDatabaseProxy.class.getSimpleName();

    private final String DATABASE_PLAYER = "users";
    private final String DATABASE_PLAYER_SCORE = "score";

    private final DatabaseReference usersRef;

    public UserDatabaseProxy(){
        super();

        usersRef = getReference().child(DATABASE_PLAYER);
    }

    /**
     * Add a user to the database. If the user id already exists, erases previously kept data
     * @param user the user to be put in the database
     */
    public void putUser(User user){
        if(user == null){
            throw new IllegalArgumentException("user should not be null");
        }

        usersRef.child(String.valueOf(user.getUserId())).setValue(user);
    }

    /**
     * Remove a user to the database.
     * @param user the user to be removed in the database
     */
    public void removeUser(User user){
        if(user == null){
            throw new IllegalArgumentException("user should not be null");
        }

        usersRef.child(String.valueOf(user.getUserId())).removeValue();
    }

    /**
     * Get the Task (asynchronous !) from data base. The user instance can be retrieved -
     * once the task is completed - by using getUserFromTask
     * @param userId
     * @return Task containing the user data
     */
    public Task<DataSnapshot> getUserTask(int userId){
        Task<DataSnapshot> task = usersRef.child(String.valueOf(userId)).get();

        task.addOnCompleteListener(task1 -> {
            if (!task1.isSuccessful()) {
                Log.e(TAG, "Error getting data", task1.getException());

            }
            else {
                Log.d(TAG, String.valueOf(task1.getResult().getValue()));
            }
        });

        return task;
    }

    /** get a user from a task
     * @param task the task containing a user
     * @return the user inside the task or null if the task is not complete
     */
    public User getUserFromTask(Task<DataSnapshot> task){
        if(task.isComplete()){
            return task.getResult().getValue(User.class);
        }
        else {
            return null;
        }

    }

    /**
     * Will trigger an event each time the user is updated in the database
     * This means that the user should be added first
     * @param user the user who's database entry will be listened
     * @param listener the listener which describes what to do on change
     */
    public void addUserListener(User user, ValueEventListener listener){
        if (listener == null || user == null){
            throw new IllegalArgumentException();
        }
        usersRef.child(String.valueOf(user.getUserId())).addValueEventListener(listener);
    }


    /**
     * Removes a ValueEventListener from a user entry in the db
     * @param user
     * @param listener
     * @throws IllegalArgumentException on null listener or null user
     */
    public void removeUserListener(User user, ValueEventListener listener){
        if (listener == null || user == null){
            throw new IllegalArgumentException();
        }
        usersRef.child(String.valueOf(user.getUserId())).removeEventListener(listener);
    }

    /**
     * Returns the top users ordered by their score from the database.
     * @param n the number of users to retrieve from the database
     * @return the task
     */
    public Task<DataSnapshot> getLeaderboardUsers(int n){
        if(n < 0){
            throw new IllegalArgumentException("n should not be negative.");
        }

        return usersRef.orderByChild(DATABASE_PLAYER_SCORE)
                .limitToLast(n)
                .get();
    }
}
