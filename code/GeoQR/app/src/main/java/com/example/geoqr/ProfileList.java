package com.example.geoqr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProfileList extends ArrayAdapter<ListEntry> {

//    private ArrayAdapter<ListEntry> list;

    private final ArrayList<ListEntry> QRList;
    private final Context context;
    FirebaseFirestore db;

    public ProfileList(@NonNull Context context, ArrayList<ListEntry> QRList) {
        super(context, 0, QRList);
        this.QRList = QRList;
        this.context = context;
    }

    /**
     * ADD SORT FUNCTIONALITY
     *
     */

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        db = FirebaseFirestore.getInstance();

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_profile_content, parent, false);
        }

        final ListEntry entry = QRList.get(position);
        final TextView content = view.findViewById(R.id.pl_content);
        final TextView score = view.findViewById(R.id.pl_score);

        content.setText(entry.getContent());
        score.setText(entry.getScore());

        return view;
    }

    public void addEntry(ListEntry entry) {
        QRList.add(entry);
    }

    public void deleteEntry(ListEntry entry) {
        if (QRList.contains(entry)) {
            QRList.remove(entry);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public int size() {
        return QRList.size();
    }
}
