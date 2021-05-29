package sdp.moneyrun.menu.leaderboards;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import sdp.moneyrun.R;
import sdp.moneyrun.menu.ListAdapterWithUser;
import sdp.moneyrun.ui.menu.leaderboards.MainLeaderboardActivity;
import sdp.moneyrun.user.User;

public class MainLeaderboardListAdapter extends ListAdapterWithUser {

    // Medal emotes
    private final String[] rank = {"\uD83E\uDD47", "\uD83E\uDD48", "\uD83E\uDD49"};
    private final int COLOR_GOLD = Color.rgb(235, 116, 52);

    public MainLeaderboardListAdapter(Activity context, ArrayList<User> userList, User user) {
        super(context, userList, user);
    }

    @SuppressLint("ViewHolder")
    public View getView(int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.main_leaderboard_item_layout, parent, false);
        User user = getItem(position);
        TextView user_position = view.findViewById(R.id.main_player_position);
        TextView user_name = view.findViewById(R.id.main_player_name);
        TextView user_score = view.findViewById(R.id.main_player_score);

        String text_position;
        if(position < rank.length) {
            text_position = rank[position];
        }else if(position >= MainLeaderboardActivity.NUM_PLAYERS_LEADERBOARD && user.equals(getCurrentUser())) {
            text_position = " -";
        }else{
            text_position = " " + (position + 1);
        }

        user_position.setText(text_position);
        user_name.setText(String.valueOf(user.getName()));
        if(user.equals(getCurrentUser())) {
            user_name.setTextColor(COLOR_GOLD);
            user_name.setTypeface(user_name.getTypeface(), Typeface.BOLD);
        }
        user_score.setText(String.valueOf(user.getMaxScoreInGame()));

        return view;
    }
}
