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

/**
 * And Adapter class for showing a user's QRs in
 * the user profile fragment in Scoreboard.
 */
public class ScoreboardQRAdapter extends ArrayAdapter<QR> {

    private ArrayList<QR> qrList;
    private ArrayList<QR> qrSeen;
    private Context ctx;

    public ScoreboardQRAdapter(Context context, ArrayList<QR> qrList, ArrayList<QR> qrSeen) {
        super(context, 0, qrList);

        this.qrList = qrList;
        this.qrSeen = qrSeen;
        this.ctx = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.list_scoreboard_qr, parent, false);

        QR qr = qrList.get(position);
        TextView code = view.findViewById(R.id.list_scoreboard_qr_contents);
        TextView score = view.findViewById(R.id.list_scoreboard_qr_score);
        TextView seen = view.findViewById(R.id.list_scoreboard_qr_seen);

        code.setText(qr.getId());
        score.setText(String.valueOf(qr.getScore()));

        if (qrSeen.contains(qr)) {
            seen.setText("Yes");
        }
        else {
            seen.setText("No");
        }

        return view;
    }
}
