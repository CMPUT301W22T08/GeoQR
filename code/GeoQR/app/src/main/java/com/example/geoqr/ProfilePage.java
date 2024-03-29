package com.example.geoqr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
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
import java.util.Collections;
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
    ArrayList<Integer> list_temp = new ArrayList<>();

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    int check_dialog;

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

        // obtain username
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        // shaking event
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                if (check_dialog == 0) { // to be implemented as the show alert dialog
                    check_dialog = 1;
                    AlertDialog.Builder alert = new AlertDialog.Builder(ProfilePage.this);
                    AlertDialog alertDialog = alert.create();
                    if (!alertDialog.isShowing()) {
                        alert.setTitle("Logout Confirmation");
                        alert.setMessage(String.format("Are you sure you want to Logout '%s'?", username));
                        alert.setPositiveButton(android.R.string.yes, (dialogInterface, i1) -> {
                            Intent log_page = new Intent(ProfilePage.this, LoginPage_V2.class);
                            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            check_dialog = 0;
                            editor.remove("username");
                            // editor.clear();
                            editor.apply();
                            Toast.makeText(getApplicationContext(), String.format("%s has been logged out", username), Toast.LENGTH_LONG).show();
                            log_page.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(log_page);
                        });
                        alert.setNegativeButton(android.R.string.no, (dialogInterface, i1) -> {
                            dialogInterface.cancel();
                            check_dialog = 0;
                        });
                        alert.show();
                    }
                }
            }
        });

        // update the view
        updateView();

        // delete the item from the list
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
                alert.setMessage("Are you sure you want to delete this item?");
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
                            }
                        }).addOnFailureListener(new OnFailureListener() {
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

                                    if (Integer.parseInt(current_score) == Integer.parseInt(largestScore)) {
                                        updateScore();
                                    }
                                    else if (Integer.parseInt(current_score) == Integer.parseInt(smallestScore)) {
                                        updateScore();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "QR - User Delete Failed");
                                }
                            });
                });
                alert.setNegativeButton(android.R.string.no, ((dialogInterface, i1) -> dialogInterface.cancel()));
                alert.show();
                return true;
            }
        });

        // click to get inside the detail
        profileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListEntry list = entryDataList.get(i);
                Intent details = new Intent(ProfilePage.this, ProfileDetails.class);
                details.putExtra("QR", list.getQrcode());
                startActivity(details);
            }
        });

        // button that goes back to the scanQR
        final FloatingActionButton returnButton = findViewById(R.id.return_to_camera);
        returnButton.setOnClickListener((v) -> {
            Intent intent = new Intent(ProfilePage.this, ScanQR.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        // help button to go to the manual
        final FloatingActionButton helpButton = findViewById(R.id.manual);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent manual = new Intent(ProfilePage.this, Manual.class);
                startActivity(manual);
            }
        });

        // to generate the users QR code
        Button generateUserQR = findViewById(R.id.generate_login_qr);
        generateUserQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateQRCode(username);
            }
        });

        // to generate the status QR code
        Button generateStatusQR = findViewById(R.id.generate_status_qr);
        generateStatusQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateQRCode(username);
            }
        });

        // edit the contact of the user
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

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    /**
     * view update method
     */
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

    /**
     * let the list adapter know the update
     */
    public void informUpdate() {
        listAdapter.notifyDataSetChanged();
    }

    /**
     * generate the QR code method
     * @param content
     * the content that the user wants to generate
     */
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

    /**
     * updating the score when the user deletes a QR code (all the scores and total codes)
     */
    private void updateScore() {
        // Users (collection) -> username (document) -> QR codes (sub-collection) -> hex (document) -> score (field)
        db.collection("Users").document(username).collection("QR codes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }
                        list_temp.clear();
                        assert value != null;
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc != null) {
                                String score = doc.getString("Score");
                                assert score != null;
                                int int_score = Integer.parseInt(score);
                                list_temp.add(int_score);
                            }
                            else {
                                System.out.println("doc == null");
                            }
                        }
                        Collections.sort(list_temp);
                        int sum = 0;
                        for(int i = 0; i < list_temp.size(); i++)
                            sum += list_temp.get(i);

                        if (list_temp.isEmpty()) {
                            db.collection("Users").document(username).update("Highest Score", String.valueOf(0));
                            db.collection("Users").document(username).update("Lowest Score", String.valueOf(0));
                            db.collection("Users").document(username).update("Total Score", String.valueOf(0));
                            highScore.setText(String.format("Highest Score: %s", "0"));
                            lowScore.setText(String.format("Lowest Score: %s", "0"));
                            profileTotal.setText(String.format("Total Score: %s", "0"));
                        }
                        else {
                            db.collection("Users").document(username).update("Highest Score", String.valueOf(list_temp.get(list_temp.size() - 1)));
                            db.collection("Users").document(username).update("Lowest Score", String.valueOf(list_temp.get(0)));
                            db.collection("Users").document(username).update("Total Score", String.valueOf(sum));
                            highScore.setText(String.format("Highest Score: %s", list_temp.get(list_temp.size() - 1)));
                            lowScore.setText(String.format("Lowest Score: %s", list_temp.get(0)));
                            profileTotal.setText(String.format("Total Score: %s", sum));
                        }
                    }
                });
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