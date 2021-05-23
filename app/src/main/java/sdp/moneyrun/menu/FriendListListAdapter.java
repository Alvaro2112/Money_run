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
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import sdp.moneyrun.Helpers;
import sdp.moneyrun.R;
import sdp.moneyrun.database.GameDatabaseProxy;
import sdp.moneyrun.game.Game;
import sdp.moneyrun.game.GameRepresentation;
import sdp.moneyrun.location.AndroidLocationService;
import sdp.moneyrun.location.LocationRepresentation;
import sdp.moneyrun.location.LocationService;
import sdp.moneyrun.user.User;

public class FriendListListAdapter extends ListAdapterWithUser {

    private Game friendGame = null;
    private AndroidLocationService locationService;

    private Button button;
    private User userRequested;

    public static final String TAG_BUTTON_PREFIX = "button";

    public FriendListListAdapter(Activity context, List<User> userList, User user, AndroidLocationService locationService) {
        super(context, userList, user);

        this.locationService = locationService;
    }

    @SuppressLint("ViewHolder")
    public View getView(int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.friend_list_item_layout, parent, false);
        userRequested = getItem(position);

        TextView userNameView = view.findViewById(R.id.friend_list_name);
        TextView playedView = view.findViewById(R.id.friend_list_n_played_result);
        TextView maxScoreView = view.findViewById(R.id.friend_list_logo_max_score_result);

        userNameView.setText(String.valueOf(userRequested.getName()));
        playedView.setText(String.valueOf(userRequested.getNumberOfPlayedGames()));
        maxScoreView.setText(String.valueOf(userRequested.getMaxScoreInGame()));

        //Define button
        button = view.findViewById(R.id.friend_list_join_game);
        Helpers.setInvalidButtonType(button);
        updateJoinButton(userRequested, button);

        //Define a tag to recognize the user.
        view.setTag(userRequested.getUserId());
        button.setTag(TAG_BUTTON_PREFIX + userRequested.getUserId());

        return view;
    }

    /**
     * Define the join game button as valid or invalid depending on predicates
     * @param userRequested the friend that may have a game
     * @param button the join button
     */
    private void updateJoinButton(@NonNull User userRequested,
                                  @NonNull Button button){
        Task<DataSnapshot> gameTask = getTaskFriendGame(userRequested);
        gameTask.addOnCompleteListener(task -> {
            if(task.isSuccessful() && friendGameIsJoinable()) {
                Helpers.setValidButtonType(button);
                button.setOnClickListener(v -> addFriendButtonImplementation());
            }
        });
    }

    /**
     * @return true if the user can join the friend's game
     */
    private boolean friendGameIsJoinable(){
        if(friendGame == null){
            return false;
        }

        Location gameLocation = friendGame.getStartLocation();
        if(gameLocation == null){
            return false;
        }

        LocationRepresentation gameLocationRepr = new LocationRepresentation(gameLocation);
        LocationRepresentation userLocation = locationService.getCurrentLocation();
        if(userLocation == null){
            return false;
        }
        double distance = gameLocationRepr.distanceTo(userLocation);

        return distance <= MenuImplementation.MAX_DISTANCE_TO_JOIN_GAME;
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
            try{
                friendGame = db.getGameFromTaskSnapshot(task);
            }catch(IllegalArgumentException e){
                friendGame = null;
            }
        });

        return gameTask;
    }

    /**
     * Add button interaction to join game lobby
     */
    private void addFriendButtonImplementation(){
        LocationRepresentation gameLocationRep = new LocationRepresentation(friendGame.getStartLocation());

        GameRepresentation gameRepresentation = new GameRepresentation(friendGame.getId(),
                friendGame.getName(),
                friendGame.getPlayerCount(),
                friendGame.getMaxPlayerCount(),
                gameLocationRep);
        Helpers.joinLobbyFromJoinButton(gameRepresentation,
                FirebaseDatabase.getInstance().getReference(),
                (Activity) getContext(),
                getCurrentUser());
    }
}
