package com.example.geoqr;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class addQR extends AppCompatActivity {
    //DATABASE STILL HAVE TROUBLE SETTING UP

    // Define values that's gonna display on the xml
    private String UserName;
    private String QRHex; // I still don't get what this is asking, the really long one or the value?
    private Integer QRScore;
    private String Comments;
    private Location location = new Location(""); // get location somehow, work with Julian
    private Boolean add_img = false;
    private Boolean add_g = false;

    // Define variables that's related with external links like db/intent
    private byte[] qr_byte;
    private CalculateScore score;
    FirebaseFirestore db;

    // Define variables that's going to be used inside this class
    TextView UNDisplay;
    TextView QRHexDisplay;
    TextView QRScoreDisplay;
    TextView GeoDisplay;
    EditText comment;
    Button add_btn;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch add_geo;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch add_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aqq_qr_code_v2);

        db = FirebaseFirestore.getInstance();

        UNDisplay = findViewById(R.id.UserAutoFill);
        QRHexDisplay = findViewById(R.id.QRAutoFill);
        QRScoreDisplay = findViewById(R.id.QRScoreAutoFill);
        GeoDisplay = findViewById(R.id.GeoSharable);
        comment = findViewById(R.id.comments);
        add_btn = findViewById(R.id.AddBtn);
        add_geo = findViewById(R.id.add_geo_switch);
        add_photo = findViewById(R.id.add_photo_switch);

        // Call from Camera class
        Intent intent = getIntent();
        qr_byte = intent.getByteArrayExtra("image");
        //UserName = intent.getDataString("");
        //////////////temporary!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        UserName = "I";
//        Bundle bundle = intent.getBundleExtra("");
//        UserName = bundle.getString("");
//        qr_byte = bundle.getByteArray("image");

        //Calculate score
        score = new CalculateScore(qr_byte);
        QRScore = score.find_total();

        // Set text on display
        UNDisplay.setText(UserName);
        QRScoreDisplay.setText(String.valueOf(QRScore));
        QRHexDisplay.setText(score.getQRHex());

        //////////////temporary!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        GeoDisplay.setText("position");

        // get if user wants to add the geo or not
        add_geo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                add_g = true;
                // somehow get the location object form other class
                // GeoDisplay.setText("the return string");
            }
        });
        // get if user wants to add the image or not
        add_photo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                add_img = true;
            }
        });



        String s = score.getHex_result();

        // define for add to database
        final CollectionReference user_Ref = db.collection("Users");
        final CollectionReference QR_ref = db.collection("QR codes");
        final DocumentReference QR_code_ref = db.collection("QR codes").document(s);

        // get all data to the QR database and go back

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 98 83 106
                // 186

                // https://www.youtube.com/watch?v=y2op1D0W8oE
                // Add to Qr collection

                List<String> loc = new ArrayList<>();
                List<String> user = new ArrayList<>();
                loc.add(GeoDisplay.getText().toString());
                user.add(UserName);
                // add data for the QR
                HashMap<String, Object> data_qr = new HashMap<>();
                data_qr.put("Locations",loc);  //(List<String>)
                data_qr.put("Score",QRScore);
                data_qr.put("User",user);

                // ID: score.getHex_result(),
                // add new doc/ override existing
                QR_ref.document(score.getHex_result()).set(data_qr,SetOptions.merge())
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

//                // update list
                // all not work
                // check method 1
//                QR_ref.document(score.getHex_result()).update("Locations", FieldValue.arrayUnion(GeoDisplay.getText().toString()));
//                QR_ref.document(score.getHex_result()).update("Users", FieldValue.arrayUnion(UserName));
//
                // check method 2
//                QR_code_ref.get().addOnCompleteListener(@NonNull Task<DocumentSnapshot> task){
//
//                }

                // check method 3
//                List<String> qr = user_Ref.document(UserName).get().getResult().get("QR codes");


                // Add to user collection
                List<String> qr = new ArrayList<>();
                qr.add(QRHexDisplay.getText().toString());
                HashMap<String, Object> user_qr = new HashMap<>();
                user_qr.put("QR codes",qr);
//                    user_qr.put("Image")

                // using username as document
                user_Ref
                        .document(UserName)
                        .set(user_qr, SetOptions.merge());


//                user_Ref.document(UserName).update("QR codes", FieldValue.arrayUnion(QRHexDisplay.getText().toString()));
//                //update user image collection?

                goBack();
            }
         });
    }

    private void goBack(){
        Intent camera = new Intent(addQR.this, Camera.class);
        startActivity(camera);
    }
}
