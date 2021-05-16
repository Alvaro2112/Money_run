package sdp.moneyrun.ui.game;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import sdp.moneyrun.R;
import sdp.moneyrun.player.Player;
import sdp.moneyrun.ui.player.PlayerListAdapter;

public class LobbyPlayerListAdapter extends PlayerListAdapter {

    public LobbyPlayerListAdapter(Activity context, ArrayList<Player> playerList) {
        super(context, playerList);
    }

    @Nullable
    public View getView(int position, @Nullable View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.lobby_player_item_layout, parent, false);
        }
        Player player = getItem(position);
        TextView player_name = view.findViewById(R.id.player_name_lobby_item);
        player_name.setText(String.valueOf(player.getName()));
        return view;
    }
}


