package sdp.moneyrun.menu;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import sdp.moneyrun.R;
import sdp.moneyrun.player.Player;

public class MainLeaderboardListAdapter extends ArrayAdapter<Player> {

    private final String[] rank = {"\uD83E\uDD47", "\uD83E\uDD48", "\uD83E\uDD49"};

    Player user;

    public MainLeaderboardListAdapter(Activity context, ArrayList<Player> playerList, Player user) {
        super(context,0 , playerList);
        if(user == null){
            throw new IllegalArgumentException("user should not be null.");
        }

        this.user = user;
    }

    public View getView(int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.leaderboard_item_layout, null, true);
        Player player = getItem(position);
        TextView player_position = (TextView) view.findViewById(R.id.main_leaderboard_player_position);
        TextView player_name = (TextView) view.findViewById(R.id.main_leaderboard_player_name);
        TextView player_score = (TextView) view.findViewById(R.id.main_leaderboard_player_score);

        String text_position;
        if(position < 3){
            text_position = rank[position];
        }
        text_position = " " + (position + 1);

        player_position.setText(text_position);
        player_name.setText(String.valueOf(player.getName()));
        player_score.setText(String.valueOf(player.getScore()));

        return view;
    }
}
