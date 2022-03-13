package com.example.geoqr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProfileList extends ArrayAdapter<ListEntry> {

    private ArrayList<ListEntry> qrlist;
    private Context context;
    FirebaseFirestore db;

    public ProfileList(Context context, ArrayList<ListEntry> qrlist) {
        super(context, 0, qrlist);
        this.qrlist = qrlist;
        this.context = context;
    }

    /*
     * ADD SORT FUNCTIONALITY
     *
     */



    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        db = FirebaseFirestore.getInstance();

        final CollectionReference collectionReference = db.collection("Users");
        final String TAG = "Sample";

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_profile_content, parent, false);
        }

        final ListEntry entry = qrlist.get(position);

        final TextView content = view.findViewById(R.id.pl_content);
        final TextView score = view.findViewById(R.id.pl_score);
        final TextView location = view.findViewById(R.id.pl_location);
        final TextView time = view.findViewById(R.id.pl_time);

        content.setText(entry.getContent());
        score.setText(entry.getScore());
        location.setText(entry.getLocation());
        time.setText(entry.getTime());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = ((FragmentActivity) view.getContext()).getSupportFragmentManager();
                new ListFragment().newInstance(entry).show(manager, "ENTRY FRAGMENT");
            }
        });


        return view;

    }
}
