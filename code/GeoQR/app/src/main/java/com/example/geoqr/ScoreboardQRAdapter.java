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

public class ScoreboardQRAdapter extends ArrayAdapter<QR> {

    private ArrayList<QR> qrList;
    private Context ctx;

    public ScoreboardQRAdapter(@NonNull Context context, ArrayList<QR> qrList) {
        super(context, 0, qrList);

        this.qrList = qrList;
        this.ctx = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.list_scoreboard_qr, parent, false);

        QR qr = qrList.get(position);
        TextView code = view.findViewById(R.id.list_scoreboard_qr_contents);
        TextView score = view.findViewById(R.id.list_scoreboard_qr_score);

        code.setText(qr.getContent());
        score.setText(String.valueOf(qr.getScore()));

        return view;
    }
}
