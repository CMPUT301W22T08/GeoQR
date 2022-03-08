package com.example.geoqr;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.List;

public class addQR extends AppCompatActivity{
    //DATABASE STILL HAVE TROUBLE SETTING UP

    // Define values thats gonna display on the xml
    private String UserName;
    private String QRhex; // I still don't get what this is asking, the really long one or the value?
    private Integer QRscore;
    private String Comments;
    private Location location = new Location(""); // get location somehow, work with Juliean

    // Define variables thats relateded with external links like db/intent
    private byte[] qr_byte;
    private CalculateScore sccore;
    FirebaseFirestore db;

    // Define variables thats going to be used inside this class
    TextView UNdisplay;
    TextView QRhexDisplay;
    TextView QRscoreDisplay;
    TextView GeoDisplay;
    EditText comment;
    ImageView QRimg;
    Button add_btn;
    Switch add_geo;
    Switch add_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_qr_code);

        UNdisplay = findViewById(R.id.UserAutoFill);
        QRhexDisplay = findViewById(R.id.QRautoFill);
        QRscoreDisplay = findViewById(R.id.QRscoreAutoFill);
        GeoDisplay = findViewById(R.id.GeoSharable);
        comment = findViewById(R.id.comments);
        QRimg = findViewById(R.id.imageView);
        add_btn = findViewById(R.id.Addbtn);
        add_geo = findViewById(R.id.add_geo_switch);
        add_photo = findViewById(R.id.add_photo_switch);

        // Call from Leo camera class
        Intent intent = getIntent();
        qr_byte = intent.getByteArrayExtra("image");
        //UserName = intent.getDataString("");
        //////////////temperally!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        UserName = "I";
//        Bundle bundle = intent.getBundleExtra("");
//        UserName = bundle.getString("");
//        qr_byte = bundle.getByteArray("image");

        //Calculate score
        sccore = new CalculateScore(qr_byte);
        QRscore = sccore.find_total();

        // Set text on display
        UNdisplay.setText(UserName);
        QRscoreDisplay.setText(QRscore.toString());
        QRhexDisplay.setText(sccore.getQRhex());

//        // if user wants to add the geo location
//        if (add_geo.isChecked()){
//            // somehow get the location object form other class
//            GeoDisplay.setText("the return string");
//        }
        //////////////temperally!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        GeoDisplay.setText("position");


        // if user choose to add image to the
        if (add_photo.isChecked()){
            QRimg.setVisibility(View.VISIBLE);
            // https://www.informit.com/articles/article.aspx?p=2423187
            QRimg.setImageBitmap(sccore.getBitmap());
            QRimg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }else{
            QRimg.setVisibility(View.INVISIBLE);
        }

        db = FirebaseFirestore.getInstance();
        final CollectionReference user_Ref = db.collection("Users");
        final CollectionReference QR_ref = db.collection("QR codes");
        // final CollectionReference QR_loc_ref = db.collection("QR codes").document(sccore.getHex_result()).collection("Location");
        // final CollectionReference QR_user_ref = db.collection("QR codes").document(sccore.getHex_result()).collection("User");
        final DocumentReference QR_code_ref = db.collection("QR codes").document(sccore.getHex_result());

        // get all data to the QR database and go to next page
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // https://www.youtube.com/watch?v=y2op1D0W8oE
                // Add to Qr collection

                // NOT TOO SURE IF THIS WILL WORK
                List<String> loc = (List<String>) QR_ref.document(sccore.getHex_result()).get(Source.valueOf("Locations"));
                List<String> user = (List<String>) QR_ref.document(sccore.getHex_result()).get(Source.valueOf("Users"));
                // I'm guessing that if it does not find it, then it will create a new one

                loc.add(GeoDisplay.getText().toString());
                user.add(UserName);

                // add data for the QR
                HashMap<String, Object> data_qr = new HashMap<>();
                data_qr.put("Locations",loc);
                data_qr.put("Score",QRscore);
                data_qr.put("User",user);
                // will need to do updates to store a list of user instead of just 1 user
                // I think I fixed it now, but not entirely sure

                QR_ref
                        .document(sccore.getHex_result())
                        .set(data_qr)
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
                                Log.d(TAG, "Data could not be added!" + e.toString());
                            }
                        });

                // Add to user collection

                // using username as document
                user_Ref
                        .document(UserName)
                        .collection("QR codes")
                        .add(QRhexDisplay.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Log.d(TAG, "Data has been added successfully!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // These are a method which gets executed if there’s any problem
                                Log.d(TAG, "Data could not be added!" + e.toString());
                            }
                        });
                    goBack();
            }
        });
    }

    private void goBack(){
        Intent score = new Intent(addQR.this, Camera.class);
        startActivity(score);
    }
}
