package com.example.geoqr;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

/**
 * add the QR codes details to the database
 */
public class addQR extends AppCompatActivity {
    //DATABASE STILL HAVE TROUBLE SETTING UP

    // Define values that's gonna display on the xml
    private String UserName, notice;
    private Integer QRScore;
    private Boolean add_img = false;
    private Boolean add_g  = false;
    private Location location_get;

    // Define variables that's related with external links like db/intent
    private String qr_str;
    private CalculateScore score;
    FirebaseFirestore db;
    DatabaseQR databaseQR;
    DocumentReference docRef;
    private FusedLocationProviderClient fusedLocationClient;

    // Define variables that's going to be used inside this class
    TextView QRInfo;
    TextView UNDisplay;
    TextView QRHexDisplay;
    TextView QRScoreDisplay;
    TextView GeoDisplay_long;
    TextView GeoDisplay_lati;
    EditText comment;
    Button add_btn;
    Button cancel_btn;
    Button add_img_btn;
    Button delete_img_btn;
    ImageView QR_img_view;
    Bitmap bitmap;
    ActivityResultLauncher<Intent> activityResultLauncher;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch add_geo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aqq_qr_code_v2);
        Objects.requireNonNull(getSupportActionBar()).hide();
        notice = "null";

        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        UNDisplay = findViewById(R.id.UserAutoFill);
        QRHexDisplay = findViewById(R.id.QRAutoFill);
        QRScoreDisplay = findViewById(R.id.QRScoreAutoFill);
        GeoDisplay_long = findViewById(R.id.GeoSharable_Long);
        GeoDisplay_lati = findViewById(R.id.GeoSharable_Lati);
        comment = findViewById(R.id.comments);
        add_btn = findViewById(R.id.AddBtn);
        cancel_btn = findViewById(R.id.CancelBtn);
        add_geo = findViewById(R.id.add_geo_switch);
        add_img_btn = findViewById(R.id.Add_img);
        delete_img_btn = findViewById(R.id.Delete_img);
        QR_img_view = findViewById(R.id.QRImg);
        QRInfo = findViewById(R.id.QRInfo);


        // Call from Camera class
        Intent text = getIntent();

        qr_str = text.getStringExtra("content");
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        UserName = sharedPreferences.getString("username", null);

        databaseQR = new DatabaseQR(UserName);
        //Calculate score
        score = new CalculateScore(qr_str);
        QRScore = score.find_total();

        // Set text on display
        UNDisplay.setText(UserName);
        QRScoreDisplay.setText(String.valueOf(QRScore));
        QRHexDisplay.setText(score.getQRHex());
        QRInfo.setText(qr_str);

        // get if user wants to add the geo or not
        add_geo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    add_g = true;
                    getLocationFromDevice();
                    GeoDisplay_lati.setVisibility(View.VISIBLE);
                    GeoDisplay_long.setVisibility(View.VISIBLE);
                }
                else {
                    add_g = false;
                    getLocationFromDevice();
                    GeoDisplay_lati.setVisibility(View.INVISIBLE);
                    GeoDisplay_long.setVisibility(View.INVISIBLE);
                }
            }

        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    QR_img_view.setVisibility(View.VISIBLE);
                    bitmap = (Bitmap) bundle.get("data");
                    QR_img_view.setImageBitmap(bitmap);
                }
                else {
                    bitmap = null;
                }
            }
        });

        // get if user wants to add the image or not
        add_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                add_img = true;
                if (cam.resolveActivity(getPackageManager()) != null) {
                    activityResultLauncher.launch(cam);
                }
            }
        });


        delete_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_img = false;
                QR_img_view.setVisibility(View.GONE);
            }
        });
        // get all data to the QR database and go back
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                docRef = db.collection("Users")
                        .document(UserName)
                        .collection("QR codes")
                        .document(score.getQRHex());

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean exist = task.getResult().exists();
                            if (!exist) {
                                add_user_db();
                                add_qr_db(location_get);
                                goBack(0);
                            } else {
                                Toast.makeText(getApplicationContext(), "The QR has been added before", Toast.LENGTH_LONG).show();
                                goBack(1);
                            }
                        }
                    }
                });
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack(1);
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getLocationFromDevice() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                location_get = location;
                if (location != null) {
                    GeoDisplay_long.setText(String.valueOf(location.getLongitude()));
                    GeoDisplay_lati.setText(String.valueOf(location.getLatitude()));
                } else {
                    Toast.makeText(getApplicationContext(), "This device does not support geolocation recording", Toast.LENGTH_LONG).show();
                    GeoDisplay_long.setText(notice);
                    GeoDisplay_lati.setText(notice);
                }
            }
        });
    }

    /**
     * go back to the class intent from
     */
    private void goBack(int code){
        Intent camera = new Intent(addQR.this, ScanQR.class);
        System.out.println("Checkpoint 0");
        if (code == 0) {
            Toast.makeText(getApplicationContext(), "Data saved successfully", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Data is not saved", Toast.LENGTH_LONG).show();
        }
        camera.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(camera);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     * add the data to the user section of firestore
     */
    public void add_user_db() {
        // only add score if sub-array in Users, QR codes, if the document does not exist
        docRef = db.collection("Users")
                .document(UserName)
                .collection("QR codes")
                .document(score.getQRHex());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    docRef.set(user_db_content(),SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "Added");
                                }})
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Not added");
                                }
                            });
                    total_score_and_count();
                }
            }
        });
    }


    /**
     * prepare the data to be added to the user part of database
     * @return user_qr - hashmap of user content
     */
    public HashMap<String, Object> user_db_content(){
        //Add to user collection
        HashMap<String, Object> user_qr = new HashMap<>();
        user_qr.put("QR codes", QRHexDisplay.getText().toString());
        user_qr.put("Comment",comment.getText().toString());
        user_qr.put("Date", getCurrentTime());
        user_qr.put("Content", qr_str);
        user_qr.put("Score", String.valueOf(QRScore));

        // if user wants to add photo
        // to be edited
        // if you do not add the image, nothing will crash
        if (add_img) {
            // got bitmap and can store to database
            // but currently no place to put bitmap on database so implement later
            if (bitmap == null) {
                user_qr.put("Bytes Array", "null");
            }
            else {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bytes = stream.toByteArray();
                // user_qr.put("Bytes Array", bytes);

                Gson gson = new Gson();
                String byte_array = gson.toJson(bytes);
                user_qr.put("Bytes Array", byte_array);
            }
        }
        if (!add_img) {
            user_qr.put("Bytes Array", "null");
        }

        return user_qr;
    }

    /**
     * prepare the data to be added to the qr part of database
     * @return data_qr - hashmap of qr content
     */
    public HashMap<String, Object> qr_db_content(){
        // https://www.youtube.com/watch?v=y2op1D0W8oE
        // Add to Qr collection

        // add data for the QR
        HashMap<String, Object> data_qr = new HashMap<>();

        data_qr.put("Score", String.valueOf(QRScore));
        data_qr.put("Content", qr_str);
        if (add_g){
            if (location_get == null) {
                data_qr.put("Latitude", "null");
                data_qr.put("Longitude", "null");
            }
            else {
                data_qr.put("Latitude", String.valueOf(location_get.getLatitude()));
                data_qr.put("Longitude", String.valueOf(location_get.getLongitude()));
            }
        }else{
            data_qr.put("Latitude", "null");
            data_qr.put("Longitude", "null");
        }


        return data_qr;
    }

    /**
     * add the data to the qr section of firestore
     */
    public void add_qr_db(Location location){
        final CollectionReference QR_ref = db.collection("QR codes");

        QR_ref.document(score.getQRHex()).set(qr_db_content(), SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // These are a method which gets executed when the task is succeeded
                        Log.d(TAG, "Data has been added successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // These are a method which gets executed if there’s any problem
                        Log.d(TAG, "Data could not be added!" + e);
                    }
                });

        HashMap<String, Object> m = new HashMap<>();



        // if user wants to add location
        if (add_g){
            if (location_get == null) {
                m.put("Latitude", "null");
                m.put("Longitude", "null");
            }
            else {
                m.put("Latitude", String.valueOf(location_get.getLatitude()));
                m.put("Longitude", String.valueOf(location_get.getLongitude()));
            }
        }
        // adding username inside the sub-collection
        QR_ref.document(score.getQRHex()).collection("Users").document(UserName)
                .set(m)  //change to hashmap
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Data of username has been added successfully!");
                    }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "~(Data of username has been added successfully!)");
                    }
                });
    }

    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzzz", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private void total_score_and_count(){
        DocumentReference docRef = db.collection("Users").document(UserName);
        docRef.get().addOnSuccessListener(new  OnSuccessListener<DocumentSnapshot>() {
              @Override
              public void onSuccess(DocumentSnapshot documentSnapshot) {

                  Integer highest_score = Integer.valueOf(Objects.requireNonNull(documentSnapshot.getString("Highest Score")));
                  Integer lowest_score = Integer.valueOf(Objects.requireNonNull(documentSnapshot.getString("Lowest Score")));
                  Integer total_score = Integer.valueOf(Objects.requireNonNull(documentSnapshot.getString("Total Score")));

                  Integer qr_score = score.find_total();

                  if (highest_score < qr_score){
                      highest_score = qr_score;
                  }
                  if(lowest_score > qr_score){
                      lowest_score = qr_score;
                  }else if(lowest_score == 0){
                      lowest_score = qr_score;
                  }
                  total_score += qr_score;

                  docRef.update("Highest Score", String.valueOf(highest_score));
                  docRef.update("Lowest Score", String.valueOf(lowest_score));
                  docRef.update("Total Score", String.valueOf(total_score));
              }
          });
    }
}
