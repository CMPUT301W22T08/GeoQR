package com.example.geoqr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;


public class ProfilePage extends AppCompatActivity implements ListFragment.OnFragmentInteractionListener {

    private ListView profileList;
    private TextView profileTotal;
    private TextView totalCodes;
    private TextView highScore;
    private TextView lowScore;
    private String username;
    ImageView show_QR;
    byte[] current;

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
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_profile_v3);

        // to be tested
        // Intent user = getIntent();
        // username = user.getStringExtra("username");
        // to be tested

        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        TextView show_username = findViewById(R.id.username);
        profileList = findViewById(R.id.profile_list);
        profileTotal = findViewById(R.id.total_score);
        totalCodes = findViewById(R.id.total_codes);
        highScore = findViewById(R.id.highest_score);
        lowScore = findViewById(R.id.lowest_score);
        show_QR = findViewById(R.id.showQR);

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



        final CollectionReference collectionReference = db.collection("Users").document(username).collection("QR codes");

        collectionReference.addSnapshotListener((queryDocumentSnapshots, error) -> {
            entryDataList.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Log.d(TAG, String.valueOf(doc.getData().get("QR codes")));
                String content = (String) doc.getData().get("Content");
                String score = (String) doc.getData().get("Score");
//                int intScore = Integer.parseInt(score);

                String time = (String) doc.getData().get("Time");
                String location = (String) doc.getData().get("Location");
                String qrcode = (String) doc.getId();

//                totalScore = totalScore + intScore;
//
//                if (intScore > largestScore) {
//                    largestScore = intScore;
//                }
//                if (smallestScore == 0 || intScore < smallestScore) {
//                    smallestScore = intScore;
//                }

                entryDataList.add(new ListEntry(qrcode, content, score, location, time));
            }
            listAdapter.notifyDataSetChanged();
            show_username.setText(username);
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
                generateQRCode(username);
            }
        });

        Button generateStatusQR = findViewById(R.id.generate_status_qr);
        generateStatusQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // to be done (generate a QR)
                String status = String.format("Username: %s\nTotal Score: %s\nTotal Scans: %s\n" +
                        "Highest Score: %s\nLowest Score: %s", username, totalScore, entryDataList.size(),
                        largestScore, smallestScore);
                generateQRCode(status);
            }
        });

        Button logout = findViewById(R.id.logout_btn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent log_page = new Intent(ProfilePage.this, LoginPage.class);
                // to be tested
                SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                Toast.makeText(getApplicationContext(), String.format("%s has been logged out", username), Toast.LENGTH_LONG).show();
                log_page.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(log_page);
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
                .document(username)
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

    private void generateQRCode(String content) {

        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
            show_QR.setImageBitmap(bitmap);
            show_QR.setVisibility(View.VISIBLE);
        }
        catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public byte[] getByteArray() {
        Gson gson = new Gson();
        db.collection("Users").document(username).collection("QR codes").document("c7be1ed902fb8dd4d48997c6452f5d7e509fbcdbe2808b16bcf4edce4c07d14e").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String obj = documentSnapshot.getString("Bytes Array");
                        current = gson.fromJson(obj, byte[].class);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
        System.out.println(Arrays.toString(current));
        return current;
    }
}