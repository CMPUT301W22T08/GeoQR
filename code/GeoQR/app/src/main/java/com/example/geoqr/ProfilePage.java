package com.example.geoqr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class ProfilePage extends AppCompatActivity implements ListFragment.OnFragmentInteractionListener {

    ListView profileList;
    TextView profileTotal;
    TextView totalCodes;
    TextView highScore;
    TextView lowScore;

    ArrayAdapter<ListEntry> listAdapter;
    ArrayList<ListEntry> entryDataList;
    final String TAG = "Sample";
    FirebaseFirestore db;
    int totalScore = 0;
    int largestScore = 0;
    int smallestScore = 0;

    ProfileList profilelist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileList = findViewById(R.id.profile_list);
        profileTotal = findViewById(R.id.total_score);
        totalCodes = findViewById(R.id.total_codes);
        highScore = findViewById(R.id.highest_score);
        lowScore = findViewById(R.id.lowest_score);

        db = FirebaseFirestore.getInstance();


        entryDataList = new ArrayList<>();
        listAdapter = new ProfileList(this, entryDataList);

        profileList.setAdapter(listAdapter);


        final CollectionReference collectionReference = db.collection("Users").document("3FmLnxuiGMAJxStHRqMq").collection("QR codes");




        collectionReference.addSnapshotListener((queryDocumentSnapshots, error) -> {
            entryDataList.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Log.d(TAG, String.valueOf(doc.getData().get("QR codes")));
                String content = (String) doc.getData().get("Content");
                String score = (String) doc.getData().get("Score");
                int intScore = Integer.parseInt(score);

                String time = (String) doc.getData().get("Time");
                String location = (String) doc.getData().get("Location");
                String qrcode = (String) doc.getId();

                totalScore = totalScore + intScore;

                if (intScore > largestScore) {
                    largestScore = intScore;
                }
                if (smallestScore == 0 || intScore < smallestScore) {
                    smallestScore = intScore;
                }

                entryDataList.add(new ListEntry(qrcode, content, score, location, time));
            }

            listAdapter.notifyDataSetChanged();
            profileTotal.setText(String.valueOf(totalScore));
            totalCodes.setText(String.valueOf(entryDataList.size()));
            highScore.setText(String.valueOf(largestScore));
            lowScore.setText(String.valueOf(smallestScore));


        });


    }


    @Override
    public void onDeletePressed(ListEntry entry) {

        int removeScore = Integer.parseInt(entry.getScore());
        totalScore = totalScore - removeScore;
        entryDataList.remove(entry);
        listAdapter.notifyDataSetChanged();


        db.collection("Users")
                .document("3FmLnxuiGMAJxStHRqMq")
                .collection("QR codes")
                .document(entry.getQrcode())
                .delete()
                .addOnSuccessListener((OnSuccessListener) (unused) -> {
                    Log.d(TAG, "Document has been deleted");
                })
                .addOnFailureListener((e) -> {
                    Log.d(TAG, "Document cannot be deleted");
                });
    }
}