package com.example.geoqr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class ProfilePage extends AppCompatActivity implements ListFragment.OnFragmentInteractionListener {

    private ListView profileList;
    private TextView profileTotal;
    private TextView totalCodes;
    private TextView highScore;
    private TextView lowScore;

    private ArrayAdapter<ListEntry> listAdapter;
    private ArrayList<ListEntry> entryDataList;
    private final String TAG = "Sample";
    FirebaseFirestore db;
    int totalScore = 0;
    int largestScore = 0;
    int smallestScore = 0;

    ProfileList profilelist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile_v3);

        profileList = findViewById(R.id.profile_list);
        profileTotal = findViewById(R.id.total_score);
        totalCodes = findViewById(R.id.total_codes);
        highScore = findViewById(R.id.highest_score);
        lowScore = findViewById(R.id.lowest_score);

        db = FirebaseFirestore.getInstance();


        entryDataList = new ArrayList<>();
        listAdapter = new ProfileList(this, entryDataList);

        profileList.setAdapter(listAdapter);

        final FloatingActionButton returnButton = findViewById(R.id.return_to_camera);
        returnButton.setOnClickListener((v) -> {
            Intent intent = new Intent(ProfilePage.this, ScanQR.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });



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
            profileTotal.setText(String.format("Total Score: %s", totalScore));
            totalCodes.setText(String.format("Total Code: %s", entryDataList.size()));
            highScore.setText(String.format("Highest Score: %s", largestScore));
            lowScore.setText(String.format("Lowest Score: %s", smallestScore));
            
        });

        Button generateUserQR = findViewById(R.id.generate_login_qr);
        generateUserQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // to be done (generate a QR)
            }
        });

        Button generateStatusQR = findViewById(R.id.generate_status_qr);
        generateStatusQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // to be done (generate a QR)
            }
        });


    }


    @Override
    public void onDeletePressed(ListEntry entry) {

        int removeScore = Integer.parseInt(entry.getScore());
        totalScore = totalScore - removeScore;

        profilelist.deleteEntry(entry);

        //entryDataList.remove(entry);
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