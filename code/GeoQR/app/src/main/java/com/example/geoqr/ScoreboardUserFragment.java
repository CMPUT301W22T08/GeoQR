package com.example.geoqr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ScoreboardUserFragment extends DialogFragment {

    User user;
    ScoreboardQRAdapter qrAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = (User) getArguments().get("User");
        qrAdapter = new ScoreboardQRAdapter(getContext(), user.getQrs());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_scoreboard_user, null);

        // Set the scores in the fragment
        TextView ts = view.findViewById(R.id.fragment_scoreboard_user_total_score);
        ts.setText(String.valueOf(user.getTotalScore()));

        TextView hs = view.findViewById(R.id.fragment_scoreboard_user_highest_score);
        hs.setText(String.valueOf(user.getHighestScore()));

        TextView tq = view.findViewById(R.id.fragment_scoreboard_user_total_qr);
        tq.setText(String.valueOf(user.getTotalQrs()));

        ListView qrView = view.findViewById(R.id.fragment_scoreboard_user_qrs);
        qrView.setAdapter(qrAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        return builder
                .setView(view)
                .setTitle(user.getName())
                .setPositiveButton("Done", null)
                .create();
    }
}
