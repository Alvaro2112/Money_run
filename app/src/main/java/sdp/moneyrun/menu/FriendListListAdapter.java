package sdp.moneyrun.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.List;

import sdp.moneyrun.Helpers;
import sdp.moneyrun.R;
import sdp.moneyrun.database.GameDatabaseProxy;
import sdp.moneyrun.game.Game;
import sdp.moneyrun.game.GameRepresentation;
import sdp.moneyrun.map.LocationRepresentation;
import sdp.moneyrun.ui.menu.MenuActivity;
import sdp.moneyrun.user.User;

public class FriendListListAdapter extends ListAdapterWithUser {

    private Game friendGame = null;

    public FriendListListAdapter(Activity context, List<User> userList, User user) {
        super(context,userList, user);
    }

    @SuppressLint("ViewHolder")
    public View getView(int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.friend_list_item_layout, parent, false);
        User userRequested = getItem(position);

        TextView userNameView = view.findViewById(R.id.friend_list_name);
        TextView playedView = view.findViewById(R.id.friend_list_n_played_result);
        TextView maxScoreView = view.findViewById(R.id.friend_list_logo_max_score_result);

        userNameView.setText(String.valueOf(userRequested.getName()));
        playedView.setText(String.valueOf(userRequested.getNumberOfPlayedGames()));
        maxScoreView.setText(String.valueOf(userRequested.getMaxScoreInGame()));

        //Define button
        Button button = view.findViewById(R.id.friend_list_join_game);
        button.setOnClickListener(v -> addFriendButtonImplementation((Button) v));
        updateFriendGameAccessibility(userRequested, button);

        //Define a tag to recognize the user.
        view.setTag(userRequested.getUserId());

        return view;
    }

    private void updateFriendGameAccessibility(@NonNull User userRequested,
                                               @NonNull Button button){
        Task<DataSnapshot> gameTask = getTaskFriendGame(userRequested);
        gameTask.addOnCompleteListener(task -> {
            if(!task.isSuccessful() || (task.isSuccessful() && !friendGameIsJoinable())){
                Helpers.setInvalidButtonType(button);
            }
        });
    }

    private boolean friendGameIsJoinable(){
        if(friendGame == null){
            return false;
        }

        Location gameLocation = friendGame.getStartLocation();
        if(gameLocation == null){
            return false;
        }
        LocationRepresentation gameLocationRepr = new LocationRepresentation(gameLocation);

        double distance = gameLocationRepr.distanceTo(...);

        return friendGame != null && distance <= MenuImplementation.MAX_DISTANCE_TO_JOIN_GAME;
    }

    /**
     * Get the friend's game from database.
     * @param requestedUser the user's friend
     * @return the task retrieving the friends task from database
     */
    private Task<DataSnapshot> getTaskFriendGame(@NonNull User requestedUser){
        GameDatabaseProxy db = new GameDatabaseProxy();
        Task<DataSnapshot> gameTask = db.getGameDataSnapshot(requestedUser.getUserId());

        gameTask.addOnCompleteListener(task -> {
            if(task.getResult() == null){
                friendGame = null;
            }

            friendGame = db.getGameFromTaskSnapshot(task);
        });

        return gameTask;
    }

    private void addFriendButtonImplementation(@NonNull Button button){

    }
}
