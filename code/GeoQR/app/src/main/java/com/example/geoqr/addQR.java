package com.example.geoqr;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private String qr_str;
    private Bitmap qr_img;
    private CalculateScore score;
    FirebaseFirestore db;
    private Bitmap b;

    // Define variables that's going to be used inside this class
    TextView QRInfo;
    TextView UNDisplay;
    TextView QRHexDisplay;
    TextView QRScoreDisplay;
    TextView GeoDisplay;
    EditText comment;
    Button add_btn;
    Button add_img_btn;
    Button delete_img_btn;
    ImageView QR_img_view;
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

        UNDisplay = findViewById(R.id.UserAutoFill);
        QRHexDisplay = findViewById(R.id.QRAutoFill);
        QRScoreDisplay = findViewById(R.id.QRScoreAutoFill);
        GeoDisplay = findViewById(R.id.GeoSharable);
        comment = findViewById(R.id.comments);
        add_btn = findViewById(R.id.AddBtn);
        add_geo = findViewById(R.id.add_geo_switch);
//        add_photo = findViewById(R.id.add_photo_switch);
        add_img_btn = findViewById(R.id.Add_img);
        delete_img_btn = findViewById(R.id.Delete_img);
        QR_img_view = findViewById(R.id.QRImg);

        QRInfo = findViewById(R.id.QRInfo);


        // Call from Camera class
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        qr_str = b.getString("content");
//        qr_byte = intent.getByteArrayExtra("image");

        //UserName = intent.getDataString("");
        //////////////temporary!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        UserName = "I";

        //Calculate score
        score = new CalculateScore(qr_str);
        QRScore = score.find_total();

        // Set text on display
        UNDisplay.setText(UserName);
        QRScoreDisplay.setText(String.valueOf(QRScore));
        QRHexDisplay.setText(score.getQRHex());
        QRInfo.setText(qr_str);

        //////////////temporary!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        GeoDisplay.setText("position");

        // get if user wants to add the geo or not
        add_geo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (add_g){
                    add_g = false;
                    GeoDisplay.setText("default");
                }else{
                    add_g = true;
                    GeoDisplay.setText("current");
                }

                // somehow get the location object form other class
                // GeoDisplay.setText("the return string");
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    QR_img_view.setImageBitmap(bitmap);
                }
            }
        });

        // get if user wants to add the image or not
        add_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                add_photo();
//                Intent intent = getIntent();
//                byte[] bytes = intent.getByteArrayExtra("image");
//                add_img = true;
//                // set image to imageview
//                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//
//                QR_img_view.setImageBitmap(bitmap);

                // to be tested
                Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cam.resolveActivity(getPackageManager()) != null) {
                    activityResultLauncher.launch(cam);
                }
                QR_img_view.setVisibility(View.VISIBLE);
            }
        });

        delete_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_img = false;
                QR_img_view.setVisibility(View.GONE);
            }
        });
//        add_photo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (add_img){
//                    add_img = false;
//                }else {
//                    add_photo();
//                    // b = c.takephoto(); // private.............................................
//                    // but wont this take the photo of the summary?
//                    add_img = true;
//                }
//            }
//        });



        String s = score.getQRHex();

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

                // if user wants to add photo
                if (add_img){
                    // got bitmap and can store to database
                    // but currently no place to put bitmap on database so implement later
                }

                // if user wants to add location
                if(add_g){
                    // get location from location class and put in hashmap
                    // but currently no place to put location on database so implement later
                }

                // ID: score.getQRHex(),
                // add new doc/ override existing
                QR_ref.document(score.getQRHex()).set(data_qr,SetOptions.merge())
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



                // Add to user collection
                DocumentSnapshot s = user_Ref.document(UserName).get().getResult();
                if(s.exists()){
                    user_Ref.document(UserName).update("QR codes", FieldValue.arrayUnion(QRHexDisplay.getText().toString()));
                }else{
                    List<String> qr = new ArrayList<>();
                    qr.add(QRHexDisplay.getText().toString());
                    HashMap<String, Object> user_qr = new HashMap<>();
                    user_qr.put("QR codes",qr);
                    // user_qr.put("Image")

                    // using username as document
                    user_Ref
                            .document(UserName)
                            .set(user_qr, SetOptions.merge());
                }


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

                //Add to user collection
                List<String> qr = new ArrayList<>();
                qr.add(QRHexDisplay.getText().toString());
                HashMap<String, Object> user_qr = new HashMap<>();
                user_qr.put("QR codes", qr);

                user_Ref.document(UserName)
                        .collection("QR codes")
                        .document(QRHexDisplay.getText().toString())
                        .set(user_qr,SetOptions.merge());

                Toast.makeText(getApplicationContext(),"Add Successfully",Toast.LENGTH_LONG).show();

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

                goBack();
            }
        });
    }

    private void goBack(){
        Intent camera = new Intent(addQR.this, Camera_V2.class);
        startActivity(camera);
    }

    private void add_photo(){
        Intent i = new Intent(addQR.this, Camera.class);
        startActivity(i);
    }

}


// and of course, the testcase does not want to work with me