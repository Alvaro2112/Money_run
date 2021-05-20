package sdp.moneyrun;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.player.Player;
import sdp.moneyrun.user.User;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class Helpers {


    @NonNull
    public static PopupWindow onButtonShowPopupWindowClick(@NonNull Activity currentActivity, View view, Boolean focusable, int layoutId) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                currentActivity.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(layoutId, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window at wanted location
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        return popupWindow;
    }

    @Nullable
    public static View getView(int position, @Nullable View view, @NonNull Player player) {

        TextView player_position = view.findViewById(R.id.player_position);
        TextView player_name = view.findViewById(R.id.player_name);
        TextView player_score = view.findViewById(R.id.player_score);

        int player_pos = position + 1;

        player_position.setText(String.valueOf(player_pos));
        player_name.setText(String.valueOf(player.getName()));
        player_score.setText(String.valueOf(player.getScore()));

        return view;
    }

    public static <T> void addOrRemoveListener(@Nullable T object, @Nullable ValueEventListener listener, @NonNull DatabaseReference databaseReference, boolean remove) {
        if (listener == null || object == null)
            throw new IllegalArgumentException();

        DatabaseReference newDatabaseReference;

        if(object instanceof Player)
            newDatabaseReference = databaseReference.child(String.valueOf(((Player)object).getPlayerId()));
        else if (object instanceof User)
            newDatabaseReference = databaseReference.child(String.valueOf(((User)object).getUserId()));
        else
            throw new IllegalArgumentException("Objects need to be a User or a Player");

        if (remove)
            newDatabaseReference.removeEventListener(listener);
        else
            newDatabaseReference.addValueEventListener(listener);
    }

    @NonNull
    public static Task<DataSnapshot> addOnCompleteListener(String TAG, @NonNull Task<DataSnapshot> task){

        task.addOnCompleteListener(task1 -> {
            if (!task1.isSuccessful()) {
                Log.e(TAG, "Error getting data", task1.getException());

            } else {
                Log.d(TAG, String.valueOf(task1.getResult().getValue()));
            }
        });

        return task;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static <T, U extends ArrayAdapter<T>> void addObjectListToAdapter(@Nullable ArrayList<T> objectList, @NonNull U listAdapter){
        if (objectList == null) {
            throw new NullPointerException("List is null");
        }
        if(objectList.isEmpty()){
            return;
        }
        listAdapter.addAll(objectList);
        ArrayList<T> objects = new ArrayList<>();
        for (int i = 0; i < listAdapter.getCount(); ++i)
            objects.add(listAdapter.getItem(i));
        listAdapter.clear();

        if(objectList.get(0) instanceof User){
            bestToWorstUser((ArrayList<User>)objects);
        }else if(objectList.get(0) instanceof Player){
            bestToWorstPlayer((ArrayList<Player>)objects);
        }else{
            throw new IllegalArgumentException("List must contain Users or Players");
        }
        listAdapter.addAll(objects);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void bestToWorstPlayer(@NonNull List<Player> players) {
        players.sort((o1, o2) -> Integer.compare(o2.getScore(), o1.getScore()));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void bestToWorstUser(@NonNull List<User> users) {
        users.sort((o1, o2) -> Integer.compare(o2.getMaxScoreInGame(), o1.getMaxScoreInGame()));
    }

    /**
     * Link list adapter to the activity
     */
    public static <T> void addAdapter(@NonNull ArrayAdapter<T> ldbAdapter,
                                      @NonNull Activity activity,
                                      int viewInt){
        // The adapter lets us add item to a ListView easily.
        ListView ldbView = activity.findViewById(viewInt);
        ldbView.setAdapter(ldbAdapter);
        ldbAdapter.clear();
    }
}
