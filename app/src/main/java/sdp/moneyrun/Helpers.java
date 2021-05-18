package sdp.moneyrun;

import android.app.Activity;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


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
    public static View getView(int position, @Nullable View view, Player player) {
        TextView player_position = view.findViewById(R.id.player_position);
        TextView player_name = view.findViewById(R.id.player_name);
        TextView player_score = view.findViewById(R.id.player_score);

        int player_pos = position + 1;

        player_position.setText(String.valueOf(player_pos));
        player_name.setText(String.valueOf(player.getName()));
        player_score.setText(String.valueOf(player.getScore()));

        return view;
    }

    public static <T> void addOrRemoveListener(@Nullable T object, @Nullable ValueEventListener listener, DatabaseReference databaseReference, boolean remove) {
        if (listener == null || object == null)
            throw new IllegalArgumentException();

        DatabaseReference newDatabaseReference = null;

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

}
