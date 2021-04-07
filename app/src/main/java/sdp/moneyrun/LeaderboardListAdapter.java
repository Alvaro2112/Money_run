package sdp.moneyrun;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

public class LeaderboardListAdapter extends ArrayAdapter<Player> {

        public LeaderboardListAdapter(Activity context, ArrayList<Player> playerList) {
            super(context,0,playerList);
        }

        public View getView(int position, View view, ViewGroup parent) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.leaderboard_item_layout, null, true);
            Player player = getItem(position);
            TextView player_position = (TextView) view.findViewById(R.id.player_position);
            TextView player_name = (TextView) view.findViewById(R.id.player_name);
            TextView player_score = (TextView) view.findViewById(R.id.player_score);

            int player_pos = position + 1;
            player_position.setText(String.valueOf(player_pos));
            player_name.setText(String.valueOf(player.getName()));
            player_score.setText(String.valueOf(player.getScore()));

            return view;
        }


}
