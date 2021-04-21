package sdp.moneyrun.ui.game;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import sdp.moneyrun.R;
import sdp.moneyrun.player.Player;

public class LobbyPlayerListAdapter extends ArrayAdapter<Player> {

    public LobbyPlayerListAdapter(Activity context, ArrayList<Player> playerList) {
        super(context, 0, playerList);
    }

    public View getView(int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.lobby_player_item_layout, null, true);
        Player player = getItem(position);
        TextView player_name = (TextView) view.findViewById(R.id.player_name_lobby_item);
        player_name.setText(String.valueOf(player.getName()));
        return view;
    }

}


