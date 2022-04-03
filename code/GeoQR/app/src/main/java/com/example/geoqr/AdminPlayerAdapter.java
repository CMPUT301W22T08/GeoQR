package com.example.geoqr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class AdminPlayerAdapter extends ArrayAdapter<AdminPlayerTuple> {

    private ArrayList<AdminPlayerTuple> playerList;
    private Context context;

    public AdminPlayerAdapter(Context context, ArrayList<AdminPlayerTuple> playerList) {
        super(context, 0, playerList);
        this.playerList = playerList;
        this.context = context;
    }

    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_admin_player, parent, false);


        AdminPlayerTuple player = playerList.get(position);
        TextView name  = view.findViewById(R.id.list_admin_player_name);
        TextView score = view.findViewById(R.id.list_admin_player_score);

        name.setText(player.getName());
        score.setText(String.valueOf(player.getScore()));

        return view;
    }

    @Override
    public void add(AdminPlayerTuple playerTuple) {
        if (!playerList.contains(playerTuple)) {
            super.add(playerTuple);
        }
    }
}
