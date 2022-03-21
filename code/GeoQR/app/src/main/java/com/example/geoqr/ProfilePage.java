package com.example.geoqr;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
import java.util.List;
import java.util.Objects;


public class ProfilePage extends AppCompatActivity implements ListFragment.OnFragmentInteractionListener {

    private ListView profileList;
    private TextView profileTotal;
    private TextView totalCodes;
    private TextView highScore;
    private TextView lowScore;
    private String username;
    private EditText contact_bar;

    TextView contact_text;
    ImageView show_QR;
    String contact;
    byte[] current;


    //private ArrayList<String> testList;

    private ArrayAdapter listAdapter;
    private ArrayList<ListEntry> entryDataList;
    private final String TAG = "Sample";
    FirebaseFirestore db;
    String totalScore, largestScore, smallestScore;

    ProfileList profilelist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_profile_v3);

        db = FirebaseFirestore.getInstance();
        TextView show_username = findViewById(R.id.username);
        profileList = findViewById(R.id.profile_list);
        profileTotal = findViewById(R.id.total_score);
        totalCodes = findViewById(R.id.total_codes);
        highScore = findViewById(R.id.highest_score);
        lowScore = findViewById(R.id.lowest_score);
        show_QR = findViewById(R.id.showQR);
        contact_bar = findViewById(R.id.contact_edit);
        contact_text  = findViewById(R.id.show_contact);
        Button contact_ok = findViewById(R.id.contact_ok);
        Button contact_cancel = findViewById(R.id.contact_cancel);
        Button contact_btn = findViewById(R.id.contact_btn);

        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        DocumentReference user_ref = db.collection("Users").document(username);
        user_ref.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        contact = documentSnapshot.getString("Contact");
                        totalScore = documentSnapshot.getString("Total Score");
                        largestScore = documentSnapshot.getString("Highest Score");
                        smallestScore = documentSnapshot.getString("Lowest Score");
                        profileTotal.setText(String.format("Total Score: %s", totalScore));
                        highScore.setText(String.format("Highest Score: %s", largestScore));
                        lowScore.setText(String.format("Lowest Score: %s", smallestScore));
                        System.out.printf("%s, %s, %s, %s", contact, totalScore, largestScore, smallestScore);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("failed");
                    }
                });

        entryDataList = new ArrayList<>();
        listAdapter = new ProfileList(this, entryDataList);


//        testList = new ArrayList<>();
//        listAdapter = new ArrayAdapter<>(this, R.layout.tempcontent, testList);

        profileList.setAdapter(listAdapter);

        show_username.setText(username);

        totalCodes.setText(String.format("Total Code: %s", entryDataList.size()));

<<<<<<< HEAD
         final CollectionReference collectionReference = db.collection("Users").document(username).collection("QR codes");
=======
        final CollectionReference collectionReference = db.collection("Users").document(username).collection("QR codes");
>>>>>>> 62901f83cb53c9cafd5311bf3e2d33137e297025

        collectionReference.addSnapshotListener((queryDocumentSnapshots, error) -> {
            entryDataList.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Log.d(TAG, String.valueOf(doc.getData().get("QR codes")));
                String content = (String) doc.getData().get("Content");
                String score = (String) doc.getData().get("Score");
<<<<<<< HEAD

                String qrcode = (String) doc.getId();
                entryDataList.add(new ListEntry(qrcode, content, score));


//                testList.add(String.format("%s                  %s", score, content));
            }
            listAdapter.notifyDataSetChanged();
            totalCodes.setText(String.format("Total Code: %s", entryDataList.size()));


=======
//                int intScore = Integer.parseInt(score);

                String time = (String) doc.getData().get("Time");
                String location = (String) doc.getData().get("Location");
                String qrcode = (String) doc.getId();


                entryDataList.add(new ListEntry(qrcode, content, score, location, time));
            }
            listAdapter.notifyDataSetChanged();
            show_username.setText(username);
            profileTotal.setText(String.format("Total Score: %s", totalScore));
            totalCodes.setText(String.format("Total Code: %s", entryDataList.size()));
>>>>>>> 62901f83cb53c9cafd5311bf3e2d33137e297025
        });


        final FloatingActionButton returnButton = findViewById(R.id.return_to_camera);
        returnButton.setOnClickListener((v) -> {
            Intent intent = new Intent(ProfilePage.this, ScanQR.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        Button generateUserQR = findViewById(R.id.generate_login_qr);
        generateUserQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateQRCode(username);
            }
        });

        Button generateStatusQR = findViewById(R.id.generate_status_qr);
        generateStatusQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                Toast.makeText(getApplicationContext(), String.format("%s has been logged out", username), Toast.LENGTH_LONG).show();
                log_page.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(log_page);
            }
        });

        contact_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contact_btn.setVisibility(View.GONE);
                contact_ok.setVisibility(View.VISIBLE);
                contact_cancel.setVisibility(View.VISIBLE);
                contact_text.setVisibility(View.GONE);
                contact_bar.setVisibility(View.VISIBLE);
                if (contact.equals("null")) {
                    contact_bar.setText("");
                }
                else {
                    contact_bar.setText(contact);
                }
                contact_bar.requestFocus();
                showKeyboard(ProfilePage.this);
                contact_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hideKeyboard(ProfilePage.this);
                        contact_btn.setVisibility(View.VISIBLE);
                        contact_ok.setVisibility(View.GONE);
                        contact_cancel.setVisibility(View.GONE);
                        contact = contact_bar.getText().toString();
                        contact_bar.setVisibility(View.GONE);
                        contact_text.setVisibility(View.VISIBLE);
                        contact_text.setText(contact);
                        db.collection("Users").document(username)
                                .update("Contact", contact)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "Contact Updated Successfully");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Contact Updated Unsuccessfully");
                                    }
                                });
                        if (contact.equals("")) {
                            contact_text.setText("Click Edit Button To Add");
                        }
                    }
                });
                contact_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hideKeyboard(ProfilePage.this);
                        contact_btn.setVisibility(View.VISIBLE);
                        contact_ok.setVisibility(View.GONE);
                        contact_cancel.setVisibility(View.GONE);
                        contact_bar.setVisibility(View.GONE);
                        contact_text.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        profileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Intent intent = new Intent(ProfilePage.this, ProfileDetails.class);



                intent.putExtra("item", String.valueOf(pos));
                startActivity(intent);

            }
        });

    }

    @Override
    public void onDeletePressed(ListEntry entry) {

        int removeScore = Integer.parseInt(entry.getScore());
        int total_score = Integer.parseInt(totalScore) - removeScore;
        totalScore = String.valueOf(total_score);

        // profilelist.deleteEntry(entry);

        //entryDataList.remove(entry);
        /////////////////////////////////////added this part which might help
        listAdapter.remove(entry);
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void showKeyboard(Activity activity) {
        if (activity.getCurrentFocus().requestFocus()) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.showSoftInput(activity.getCurrentFocus(), InputMethodManager.SHOW_IMPLICIT);
        }
    }

}