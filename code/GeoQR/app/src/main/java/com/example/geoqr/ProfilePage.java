package com.example.geoqr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class ProfilePage extends AppCompatActivity {

    private ListView profileList;
    private TextView profileTotal, show_username;
    private TextView totalCodes;
    private TextView highScore;
    private TextView lowScore;
    private String username;
    private EditText contact_bar;
    TextView contact_text;
    ImageView show_QR;
    String contact, content, current_score;

    private ArrayAdapter<ListEntry> listAdapter;
    private ArrayList<ListEntry> entryDataList;
    private final String TAG = "Sample";
    FirebaseFirestore db;
    String totalScore, largestScore, smallestScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_profile_v3);
        String notice = "Click Edit Button To Add";

        db = FirebaseFirestore.getInstance();
        show_username = findViewById(R.id.username);
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

        // updateScore();
        updateView();



        profileList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListEntry list = entryDataList.get(i);
                db.collection("Users").document(username).collection("QR codes").document(list.getQrcode())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                current_score = documentSnapshot.getString("Score");




                                updateScoreTester();
//
//                                if (current_score == highScore.toString()){
//                                    updateScoreTester();
//                                }else if(current_score == highScore.toString()){
//                                    updateScoreTester();
//                                }







                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("Failed to access current score");
                            }
                        });
                AlertDialog.Builder alert = new AlertDialog.Builder(ProfilePage.this);
                alert.setTitle("Delete Confirmation");
                alert.setMessage(String.format("Are you sure you want to delete '%s'?", content));
                alert.setPositiveButton(android.R.string.yes, (dialogInterface, i1) -> {
                    listAdapter.remove(list);
                    entryDataList.remove(list);
                    informUpdate();
                    db.collection("Users")
                        .document(username)
                        .collection("QR codes")
                        .document(list.getQrcode())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "User - QR Successfully deleted");
                                System.out.println("User - QR Successfully deleted");
                            }
                        })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "User - QR Delete Failed");
                                }
                            });

                    db.collection("QR codes")
                            .document(list.getQrcode())
                            .collection("Users")
                            .document(username)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "QR - User Successfully Deleted");
                                    System.out.println("QR - User Successfully Deleted");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "QR - User Delete Failed");
                                }
                            });
                    updateView();
                    db.collection("Users").document(username)
                            .update("Total Score", String.valueOf(Integer.parseInt(totalScore) - Integer.parseInt(current_score)));
                    updateView();
                });
                alert.setNegativeButton(android.R.string.no, ((dialogInterface, i1) -> dialogInterface.cancel()));
                alert.show();
                return true;
            }
        });

        profileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListEntry list = entryDataList.get(i);
                Intent details = new Intent(ProfilePage.this, ProfileDetails.class);
                details.putExtra("QR", list.getQrcode());
                startActivity(details);
            }
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
                updateView();
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
                updateView();
                AlertDialog.Builder alert = new AlertDialog.Builder(ProfilePage.this);
                alert.setTitle("Logout Confirmation");
                alert.setMessage(String.format("Are you sure you want to Logout '%s'?", username));
                alert.setPositiveButton(android.R.string.yes, (dialogInterface, i1) -> {
                    Intent log_page = new Intent(ProfilePage.this, LoginPage.class);
                    SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    Toast.makeText(getApplicationContext(), String.format("%s has been logged out", username), Toast.LENGTH_LONG).show();
                    log_page.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(log_page);
                });
                alert.setNegativeButton(android.R.string.no, ((dialogInterface, i1) -> dialogInterface.cancel()));
                alert.show();
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
                        if (contact.equals("")) {
                            contact = "null";
                        }
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
                        if (contact.equals("null")) {
                            contact_text.setText(notice);
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
    }

    private void updateView() {
        final DocumentReference user_ref = db.collection("Users").document(username);
        user_ref.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        contact = documentSnapshot.getString("Contact");
                        totalScore = documentSnapshot.getString("Total Score");
                        largestScore = documentSnapshot.getString("Highest Score");
                        smallestScore = documentSnapshot.getString("Lowest Score");
                        if (!contact.equals("null")) {
                            contact_text.setText(contact);
                        }
                        profileTotal.setText(String.format("Total Score: %s", totalScore));
                        highScore.setText(String.format("Highest Score: %s", largestScore));
                        lowScore.setText(String.format("Lowest Score: %s", smallestScore));
                        System.out.printf("%s, %s, %s, %s\n", contact, totalScore, largestScore, smallestScore);
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
        profileList.setAdapter(listAdapter);
        show_username.setText(username);

        final CollectionReference collectionReference = db.collection("Users").document(username).collection("QR codes");
        collectionReference.addSnapshotListener((queryDocumentSnapshots, error) -> {
            entryDataList.clear();
            assert queryDocumentSnapshots != null;
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Log.d(TAG, String.valueOf(doc.getData().get("QR codes")));
                content = (String) doc.getData().get("Content");
                String score = (String) doc.getData().get("Score");
                String qrcode = doc.getId();
                entryDataList.add(new ListEntry(username, qrcode, content, score));
            }
            informUpdate();
            profileTotal.setText(String.format("Total Score: %s", totalScore));
            totalCodes.setText(String.format("Total Code: %s", entryDataList.size()));
        });
    }

    public void informUpdate() {
        listAdapter.notifyDataSetChanged();
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

    private void updateScoreTester(){
        CollectionReference Ref = db.collection("Users").document(username).collection("QR codes");
        Ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                String largest;
                ArrayList<String> s = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    s.add((String) doc.get("Score"));
                    s.add("8");
                    s.add("9");

                    largest = s.get(0);
                    for (int i = 1; i < s.size(); i++){
                        // says s is null
                        if(Integer.valueOf(largest)  < Integer.valueOf(s.get(i))){
                            largest = s.get(i);
                        }
                    }
                    HashMap<String,String> x = new HashMap<>();
                    Ref.document("temp").set(x);

                }
            }
        });

    }

    private void updateScore() {


        DocumentReference docRef = db.collection("Users").document(username);
        db.collection("Users").document(username).collection("QR codes")
                .addSnapshotListener((queryDocuments, error) -> {
                    ArrayList<Integer> list_temp = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocuments) {
                        String score = doc.getString("Score");
                        int int_score = Integer.parseInt(score);
                        System.out.println(String.format("score: %s", int_score));
                        list_temp.add(int_score);
                        list_temp.add(1);
                        list_temp.add(2);

                        Log.d(TAG, "updateScore111111111: "+list_temp);
                        Log.d(TAG, "updateScore11111111111: "+list_temp.size());
                    }
                    Log.d(TAG, "updateScore: "+list_temp+378);
                    Log.d(TAG, "updateScore: "+list_temp.size());
                });

//        final CollectionReference collectionReference = db.collection("Users").document(username).collection("QR codes");
//        final DocumentReference docRef = db.collection("Users").document(username);
//        docRef.get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        String large = documentSnapshot.getString("Highest Score");
//                        String small = documentSnapshot.getString("Lowest Score");
//                        collectionReference.addSnapshotListener((queryDocumentSnapshots, error) -> {
//                            System.out.println("I came here and go");
//                            assert queryDocumentSnapshots != null;
//                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                                Log.d(TAG, String.valueOf(doc.getData().get("QR codes")));
//                                String score = (String) doc.getData().get("Score");
//
//                                assert score != null;
//                                if (Integer.parseInt(score) > Integer.parseInt(large)) {
//                                    docRef.update("Highest Score", score);
//                                    System.out.println("large here");
//                                }
//                                if (Integer.parseInt(score) < Integer.parseInt(small)) {
//                                    docRef.update("Lowest Score", score);
//                                    System.out.println("small here");
//                                }
//                                System.out.println("going to run updateView");
//                                updateView();
//                            }
//                        });
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        System.out.println("failed");
//                    }
//                });



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