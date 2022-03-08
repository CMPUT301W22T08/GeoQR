package com.example.geoqr;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class addQR extends AppCompatActivity{
    //DATABASE STILL HAVE TROUBLE SETTING UP

    // Define values that's gonna display on the xml
    private String UserName;
    private String QRHex; // I still don't get what this is asking, the really long one or the value?
    private Integer QRScore;
    private String Comments;
    private Location location = new Location(""); // get location somehow, work with Julian

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
    ImageView QRImg;
    Button add_btn;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch add_geo;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch add_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_qr_code);

        db = FirebaseFirestore.getInstance();

        UNDisplay = findViewById(R.id.UserAutoFill);
        QRHexDisplay = findViewById(R.id.QRautoFill);
        QRScoreDisplay = findViewById(R.id.QRScoreAutoFill);
        GeoDisplay = findViewById(R.id.GeoSharable);
        comment = findViewById(R.id.comments);
        QRImg = findViewById(R.id.imageView);
        add_btn = findViewById(R.id.Addbtn);
        add_geo = findViewById(R.id.add_geo_switch);
        add_photo = findViewById(R.id.add_photo_switch);

        // Call from Leo camera class
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

//        // if user wants to add the geo location
//        if (add_geo.isChecked()){
//            // somehow get the location object form other class
//            GeoDisplay.setText("the return string");
//        }
        //////////////temporary!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        GeoDisplay.setText("position");


        // if user choose to add image to the
        if (add_photo.isChecked()) {
            QRImg.setVisibility(View.VISIBLE);
            // https://www.informit.com/articles/article.aspx?p=2423187
            QRImg.setImageBitmap(score.getBitmap());
            QRImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        else {
            QRImg.setVisibility(View.GONE);
        }

        
        final CollectionReference user_Ref = db.collection("Users");
        final CollectionReference QR_ref = db.collection("QR codes");
        // final CollectionReference QR_loc_ref = db.collection("QR codes").document(score.getHex_result()).collection("Location");
        // final CollectionReference QR_user_ref = db.collection("QR codes").document(score.getHex_result()).collection("User");
        final DocumentReference QR_code_ref = db.collection("QR codes").document(score.getHex_result());

        // get all data to the QR database and go to next page
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // https://www.youtube.com/watch?v=y2op1D0W8oE
                // Add to Qr collection

                try{
                    // update list
                    QR_ref.document(score.getHex_result()).update("Locations", FieldValue.arrayUnion(GeoDisplay.getText().toString()));
                    QR_ref.document(score.getHex_result()).update("Users", FieldValue.arrayUnion(UserName));

                } catch (Exception e) {
                    // if new doc
                    List<String> loc = new ArrayList<>();
                    List<String> user = new ArrayList<>();
                    loc.add(GeoDisplay.getText().toString());
                    user.add(UserName);
                    // add data for the QR
                    HashMap<String, Object> data_qr = new HashMap<>();
                    data_qr.put("Locations",loc);  //(List<String>)
                    data_qr.put("Score",QRScore);
                    data_qr.put("User",user);

                    // add new doc/ override existing
                    QR_ref.document(score.getHex_result()).set(data_qr)
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
                }

                // Add to user collection
                try {
                    user_Ref.document(UserName).update("QR codes", FieldValue.arrayUnion(QRHexDisplay.getText().toString()));
                } catch (Exception e) {
                    List<String> qr = new ArrayList<>();
                    qr.add(QRHexDisplay.getText().toString());
                    HashMap<String, Object> user_qr = new HashMap<>();
                    user_qr.put("QR codes",qr);

                    // using username as document
                    user_Ref
                            .document(UserName)
                            .set(user_qr, SetOptions.merge());
                }
                goBack();
            }
        });
    }

    private void goBack(){
        Intent camera = new Intent(addQR.this, Camera.class);
        startActivity(camera);
    }
}
