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

public class AdminQRAdapter extends ArrayAdapter<QR> {

    private ArrayList<QR> qrList;
    private Context context;

    public AdminQRAdapter(Context context, ArrayList<QR> qrList) {
        super(context, 0, qrList);

        this.qrList = qrList;
        this.context = context;
    }

    public View getView (final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_admin_qr, parent, false);

        QR qrt = qrList.get(position);
        TextView code = view.findViewById(R.id.list_admin_qr_code);
        TextView player = view.findViewById(R.id.list_admin_qr_player);
        TextView score = view.findViewById(R.id.list_admin_qr_score);

        code.setText(qrt.getContent());
        player.setText(qrt.getPlayer());
        score.setText(String.valueOf(qrt.getScore()));

        return view;
    }
}
