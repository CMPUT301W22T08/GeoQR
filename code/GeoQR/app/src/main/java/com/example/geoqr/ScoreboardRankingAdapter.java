package com.example.geoqr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ScoreboardRankingAdapter extends ArrayAdapter<User> {
    private ArrayList<User> users;

    Context context;
    private int board;  // Ranking board: 0, 1,or 2

    public ScoreboardRankingAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);

        this.users = users;
        this.context = context;
        this.board = 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_scoreboard_placement, parent, false);

        User user = users.get(position);
        TextView rank = view.findViewById(R.id.scoreboard_placement_rank);
        TextView username = view.findViewById(R.id.scoreboard_placement_user);
        TextView score = view.findViewById(R.id.scoreboard_placement_score);

        rank.setText(String.valueOf(position + 1));
        username.setText(user.getName());
        score.setText(String.valueOf(getScore(user)));

        return view;
    }

    /**
     * Returns the score of an user corresponding to the current ranking board
     * @param user
     * @return
     */
    private int getScore(User user) {
        int score;
        switch (board) {
            case 0: score = user.getTotalScore(); break;
            case 1: score = user.getHighestScore(); break;
            case 2: score = user.getTotalQrs(); break;
            default: score = 0;
        }

        return score;
    }

    /**
     * Sets the current ranking board
     * @param board
     */
    public void setBoard(int board) {
        this.board = board;
        this.sort();
    }

    /**
     * Sorts the user list
     */
    public void sort() {
        super.sort((user, t1) -> {
            int res = 0;
            switch (board) {
                case 0: res = user.getTotalScore() < t1.getTotalScore() ? 1 : -1; break;
                case 1: res = user.getHighestScore() < t1.getHighestScore() ? 1 : -1; break;
                case 2: res = user.getTotalQrs() < t1.getTotalQrs() ? 1 : -1; break;
            }

            return res;
        });
    }
}
