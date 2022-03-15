package com.example.geoqr;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.geoqr.RandomString;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginPage extends AppCompatActivity {

    private Button btnLogin, btnGenerate;
    private EditText etUsername;
    private FloatingActionButton scanLogin;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int CAMERA_PERMISSION_CODE = 10;
    String username_scan;
    DatabaseQR databaseQR = new DatabaseQR();
    // private CollectionReference ref = db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        ScanQR checkCam = new ScanQR();

        if (!checkCam.checkCamera(this)) {
            Toast.makeText(getApplicationContext(), "You need a camera for this app", Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, CAMERA_PERMISSION_CODE);
            }
        }


        db = FirebaseFirestore.getInstance();

        etUsername = findViewById(R.id.et_Username);
        btnGenerate = findViewById(R.id.btn_Generate);
        btnLogin = findViewById(R.id.btn_Login);
        scanLogin = findViewById(R.id.scan_login);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                Map<String, Object> data = new HashMap<>();
                data.put("Comment","");
                data.put("Content","");
                data.put("Location","");
                data.put("Score","");
                data.put("Time","");

                if (!TextUtils.isEmpty(username)) {  // if the username is not empty
                    db.collection("Users").document(username).collection("QR codes")
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                    databaseQR.setUsername(username);
                                    Intent camScan = new Intent(LoginPage.this, ScanQR.class);
                                    startActivity(camScan);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });

                } else {
                    Toast.makeText(LoginPage.this, "Username cannot be empty, please re-enter!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generate();
            }
        });

        scanLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scan = new Intent(LoginPage.this, ScanLoginQR.class);
                startActivity(scan);
            }
        });
    }

    private void test() {
        boolean check = checkIfUserExists();
        if (!check) {
            databaseQR.setUsername(username_scan);
            Intent camScan = new Intent(LoginPage.this, ScanQR.class);
            startActivity(camScan);
        }
    }

    private boolean checkIfUserExists() {
        Intent intent = getIntent();
        username_scan = intent.getStringExtra("content");
        return false;
    }


    private void generate(){
        RandomString randomString = new RandomString();
        String result = randomString.generateAlphaNumeric(12);
        etUsername.setText(result);
    }

}
