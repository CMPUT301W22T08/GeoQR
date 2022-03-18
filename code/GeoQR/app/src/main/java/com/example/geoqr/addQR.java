package com.example.geoqr;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * add the QR codes details to the database
 */
public class addQR extends AppCompatActivity {
    //DATABASE STILL HAVE TROUBLE SETTING UP

    // Define values that's gonna display on the xml
    private String UserName;
    // private String QRHex; // I still don't get what this is asking, the really long one or the value?
    private Integer QRScore;
    private String Comments;
    private Location location = new Location(""); // get location somehow, work with Julian
    private Boolean add_img = false;
    private Boolean add_g  = false;
    private Location location_get;

    // Define variables that's related with external links like db/intent
    private String qr_str;
    private CalculateScore score;
    FirebaseFirestore db;
    DatabaseQR databaseQR = new DatabaseQR();
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
//    @SuppressLint("UseSwitchCompatOrMaterialCode")
//    Switch add_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aqq_qr_code_v2);

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


        //Calculate score
        score = new CalculateScore(qr_str);
        QRScore = score.find_total();

        // Set text on display
        UNDisplay.setText(UserName);
        QRScoreDisplay.setText(String.valueOf(QRScore));
        QRHexDisplay.setText(score.getQRHex());
        QRInfo.setText(qr_str);

        //////////////temporary!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        // get if user wants to add the geo or not
        add_geo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    add_g = true;
                    GeoDisplay_lati.setVisibility(View.VISIBLE);
                    GeoDisplay_long.setVisibility(View.VISIBLE);
                    fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            location_get = location;
//                            Log.d(TAG, "long"+location);
//                            Log.d(TAG, "lati"+location);
                            if (location != null){
                                GeoDisplay_long.setText(String.valueOf(location.getLongitude()));
//                                Log.d(TAG, "long"+location.getLongitude());
//                                Log.d(TAG, "lati"+location.getLatitude());
                                GeoDisplay_lati.setText(String.valueOf(location.getLatitude()));
                            } else {
                                GeoDisplay_long.setText("Location Longitude is NULL");
                                GeoDisplay_lati.setText("Location Latitude is NULL");
                            }
                        }
                    });

                }
                else {
                    add_g = false;
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
            }
        });

        // get if user wants to add the image or not
        add_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // to be tested
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

        String s = score.getQRHex();

        // define for add to database
        final CollectionReference QR_ref = db.collection("QR codes");
        // final DocumentReference QR_code_ref = db.collection("QR codes").document(s);

        // get all data to the QR database and go back

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_qr_db();
                add_user_db();
                goBack(0);
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack(1);
            }
        });
    }


    /**
     * go back to the class intent from
     */
    private void goBack(int code){
        Intent camera = new Intent(addQR.this, ScanQR.class);
        if (code == 0) {
            Toast.makeText(getApplicationContext(), "Data saved", Toast.LENGTH_LONG).show();
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



        final CollectionReference user_Ref = db.collection("Users").document(UserName.toString())
                .collection("QR codes");

        user_Ref.document(QRHexDisplay.getText().toString())
                .set(user_db_content(),SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAdded");
                    }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNot added");
                    }
                });
    }


    /**
     * prepare the data to be added to the user part of database
     * @return user_qr - hashmap of user content
     */
    public HashMap<String, Object> user_db_content(){
        //Add to user collection
        List<String> qr = new ArrayList<>();
        qr.add(QRHexDisplay.getText().toString());
        HashMap<String, Object> user_qr = new HashMap<>();
        user_qr.put("QR codes", qr);
        user_qr.put("Comment",comment.getText().toString());

        // if user wants to add photo
        // to be edited
        if (add_img){
            // got bitmap and can store to database
            // but currently no place to put bitmap on database so implement later
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();
            user_qr.put("Bytes Array", bytes);
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

        // if user wants to add location
        if(add_g){
            data_qr.put("Location", location_get);
        }

        return data_qr;
    }

    /**
     * add the data to the qr section of firestore
     */
    public void add_qr_db(){
        final CollectionReference QR_ref = db.collection("QR codes");

//        System.out.println("Debug, something wrong");
//        Log.d("Debug", "Debug3, something wrong");
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

        // adding username inside the sub-collection
//        QR_ref.document(score.getQRHex()).collection("Users").document(databaseQR.getUserName()).set(databaseQR.getUserName())  //change to hashmap
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Log.d(TAG, "Data of username has been added successfully!");
//                    }})
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, "~(Data of username has been added successfully!)");
//                    }
//                });

        Toast.makeText(getApplicationContext(),"Add Successfully",Toast.LENGTH_LONG).show();
    }

    private void total_score_and_count(){
        final Map<String, Object>[] map = new Map[]{new HashMap<>()};
        int highest_score;
        int lowest_score;
        int total_score;

        DocumentReference docRef = db.collection("Users").document(UserName);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
              @Override
              public void onSuccess(DocumentSnapshot documentSnapshot) {
                  highest_score
              }
          });
//                                                   OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        highest_score = document.get
//
//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                    } else {
//                        Log.d(TAG, "No such document");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });
        // get user
        // get user field
        // get data
        // update data
        // store/merge/overwrite data
    }



}


// and of course, the testcase does not want to work with me

// 98 83 106
// 186



// ID: score.getQRHex(),
// add new doc/ override existing


//                // check if document exist
// all not work
// check method 1
//                QR_ref.document(score.getQRHex()).update("Locations", FieldValue.arrayUnion(GeoDisplay.getText().toString()));
//                QR_ref.document(score.getQRHex()).update("Users", FieldValue.arrayUnion(UserName));
// raise error and use try
//
// check method 2
//                QR_code_ref.get().addOnCompleteListener(@NonNull Task<DocumentSnapshot> task){
//
//                }

// check method 3
//                List<String> qr = (ArrayList<String>) user_Ref.document(UserName).get().getResult().get("QR codes");


//
//                // Add to user collection
//                DocumentSnapshot s = user_Ref.document(UserName).get().getResult();
//                if(s.exists()){
//                    user_Ref.document(UserName).update("QR codes", FieldValue.arrayUnion(QRHexDisplay.getText().toString()));
//                }else{
//                    List<String> qr = new ArrayList<>();
//                    qr.add(QRHexDisplay.getText().toString());
//                    HashMap<String, Object> user_qr = new HashMap<>();
//                    user_qr.put("QR codes",qr);
//                    // user_qr.put("Image")
//
//                    // using username as document
//                    user_Ref
//                            .document(UserName)
//                            .set(user_qr, SetOptions.merge());
//                }


//                List<Object> list = new ArrayList<>();
//
//                user_Ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()){
//                            // DOC EXIST
//
//
////                            for (QueryDocumentSnapshot document : task.getResult()){
////                                list.add(document.getId());
////                            }
//                        }else{
//                            // doc DNE
//
//                            //Add to user collection
//                            List<String> qr = new ArrayList<>();
//                            qr.add(QRHexDisplay.getText().toString());
//                            HashMap<String, Object> user_qr = new HashMap<>();
//                            user_qr.put("QR codes", qr);
//                            // user_qr.put("Image")
//                            // using username as document
//                        }
//                    }
//                });


//                final List<String>[] qr = new List[]{new ArrayList<>()};
//
//                DocumentReference s = user_Ref.document(UserName);
//                s.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()){
//                            DocumentSnapshot d = task.getResult();
//                            if (d.exists()){
//                                qr[0] = (List<String>) user_Ref.document(UserName).get(Source.valueOf("QR codes"));
//                                qr[0].add(QRHexDisplay.getText().toString());
//                                HashMap<String, Object> user_qr = new HashMap<>();
//                                user_qr.put("QR codes", qr[0]);
////                    user_qr.put("Image")
//                                // using username as document
//                                user_Ref
//                                        .document(UserName)
//                                        .set(user_qr,SetOptions.merge());
//                            }else{
//                                // Add to user collection
//                                //List<String> qr = new ArrayList<>();
//                                qr[0].add(QRHexDisplay.getText().toString());
//                                HashMap<String, Object> user_qr = new HashMap<>();
//                                user_qr.put("QR codes", qr[0]);
////                    user_qr.put("Image")
//                                // using username as document
//                                user_Ref
//                                        .document(UserName)
//                                        .set(user_qr,SetOptions.merge());
//                            }
//                        }else{
//                            Log.d(TAG, "get failed with ", task.getException());
//                        }
//                    }
//                });

//                user_Ref.document(UserName).update("QR codes", FieldValue.arrayUnion(QRHexDisplay.getText().toString()));
//                //update user image collection?

//                user_Ref.document(UserName).update("QR codes", FieldValue.arrayUnion(QRHexDisplay.getText().toString()));
//                //update user image collection?