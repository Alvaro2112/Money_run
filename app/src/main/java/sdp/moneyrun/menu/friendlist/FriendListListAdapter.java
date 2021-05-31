package sdp.moneyrun.menu.friendlist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

import sdp.moneyrun.Helpers;
import sdp.moneyrun.R;
import sdp.moneyrun.database.game.GameDatabaseProxy;
import sdp.moneyrun.game.Game;
import sdp.moneyrun.game.GameRepresentation;
import sdp.moneyrun.location.AndroidLocationService;
import sdp.moneyrun.location.LocationRepresentation;
import sdp.moneyrun.menu.ListAdapterWithUser;
import sdp.moneyrun.menu.MenuImplementation;
import sdp.moneyrun.user.User;

@SuppressWarnings({"FieldCanBeLocal", "CanBeFinal"})
public class FriendListListAdapter extends ListAdapterWithUser {

    public static final String TAG_BUTTON_PREFIX = "button";
    private final AndroidLocationService locationService;
    private final String LOCATION_MODE = LocationManager.GPS_PROVIDER;
    @NonNull
    HashMap<Integer, Game> gamesByPosition = new HashMap<>();

    public FriendListListAdapter(Activity context, List<User> userList, User user, AndroidLocationService locationService) {
        super(context, userList, user);

        this.locationService = locationService;
    }

    @SuppressLint("ViewHolder")
    public View getView(int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.friend_list_item_layout, parent, false);
        User userRequested = getItem(position);
        // Define game in hash map
        gamesByPosition.put(position, null);

        TextView userNameView = view.findViewById(R.id.friend_list_name);
        TextView playedView = view.findViewById(R.id.friend_list_n_played_result);
        TextView maxScoreView = view.findViewById(R.id.friend_list_logo_max_score_result);

        userNameView.setText(String.valueOf(userRequested.getName()));
        playedView.setText(String.valueOf(userRequested.getNumberOfPlayedGames()));
        maxScoreView.setText(String.valueOf(userRequested.getMaxScoreInGame()));

        //Define button
        Button button = view.findViewById(R.id.friend_list_join_game);
        Helpers.setInvalidButtonType(button);
        updateJoinButton(userRequested, button, position);

        //Define a tag to recognize the user.
        view.setTag(userRequested.getUserId());
        button.setTag(TAG_BUTTON_PREFIX + userRequested.getUserId());

        return view;
    }

    /**
     * Define the join game button as valid or invalid depending on predicates
     *
     * @param userRequested the friend that may have a game
     * @param button        the join button
     */
    private void updateJoinButton(@NonNull User userRequested,
                                  @NonNull Button button,
                                  int position) {
        Task<DataSnapshot> gameTask = getTaskFriendGame(userRequested, position);
        gameTask.addOnCompleteListener(task -> {
            Game friendGame = gamesByPosition.get(position);

            if (task.isSuccessful() && friendGameIsJoinable(friendGame)) {
                Helpers.setValidButtonType(button);
                button.setOnClickListener(v -> addFriendButtonImplementation(friendGame));
            }
        });
    }

    /**
     * @return true if the user can join the friend's game
     */
    private boolean friendGameIsJoinable(@Nullable Game friendGame) {
        if (friendGame == null) {
            return false;
        }

        Location gameLocation = friendGame.getStartLocation();
        if (gameLocation == null) {
            return false;
        }

        LocationRepresentation gameLocationRepr = new LocationRepresentation(gameLocation);
        LocationRepresentation userLocation = locationService.getCurrentLocation();
        if (userLocation == null) {
            return false;
        }

        double distance = gameLocationRepr.distanceTo(userLocation);
        return distance <= MenuImplementation.MAX_DISTANCE_TO_JOIN_GAME;
    }

    /**
     * Get the friend's game from database.
     *
     * @param requestedUser the user's friend
     * @return the task retrieving the friends task from database
     */
    @NonNull
    private Task<DataSnapshot> getTaskFriendGame(@NonNull User requestedUser, int position) {
        GameDatabaseProxy db = new GameDatabaseProxy();
        Task<DataSnapshot> gameTask = db.getGameDataSnapshot(requestedUser.getUserId());

        gameTask.addOnCompleteListener(task -> {
            try {
                gamesByPosition.put(position, db.getGameFromTaskSnapshot(task));
            } catch (IllegalArgumentException e) {
                gamesByPosition.put(position, null);
            }
        });

        return gameTask;
    }

    /**
     * Add button interaction to join game lobby
     */
    private void addFriendButtonImplementation(@Nullable Game friendGame) {
        if (friendGame == null) {
            return;
        }
        if (getCurrentUser() == null) {
            return;
        }

        LocationRepresentation gameLocationRep = new LocationRepresentation(friendGame.getStartLocation());

        GameRepresentation gameRepresentation = new GameRepresentation(friendGame.getId(),
                friendGame.getName(),
                friendGame.getPlayerCount(),
                friendGame.getMaxPlayerCount(),
                gameLocationRep);
        Helpers.joinLobbyFromJoinButton(gameRepresentation,
                FirebaseDatabase.getInstance().getReference(),
                (Activity) getContext(),
                getCurrentUser(),
                LOCATION_MODE);
    }
}
