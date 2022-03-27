package com.example.geoqr;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;

import java.util.Objects;

// this class is to be implemented

/**
 * this class is for login scanning
 */
public class ScanLoginQR extends AppCompatActivity {

    private CodeScanner lCodeScanner;
    private String content;
    private CodeScannerView scanLogin;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_login);
        Objects.requireNonNull(getSupportActionBar()).hide();
        db = FirebaseFirestore.getInstance();
        scanLogin = findViewById(R.id.login_view);
        FloatingActionButton cancel = findViewById(R.id.cancel_btn_login);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getApplicationContext(), "Camera Permission Needed", Toast.LENGTH_LONG).show();
            finish();
            // ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, CAMERA_PERMISSION_CODE);
        }
        else {
            scanLogin();
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(ScanLoginQR.this, LoginPage.class);
                login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(login);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    /**
     * provides camera to scan the user's QR code
     */
    private void scanLogin() {
        lCodeScanner = new CodeScanner(this, scanLogin);
        lCodeScanner.setAutoFocusEnabled(true);
        lCodeScanner.setFormats(CodeScanner.ALL_FORMATS);
        lCodeScanner.setScanMode(ScanMode.CONTINUOUS);
        lCodeScanner.setFlashEnabled(false);

        lCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        content = result.getText();
                        checkIfAdmin(content);
//                        Intent passBack = new Intent(ScanLoginQR.this, LoginPage.class);
//                        passBack.putExtra("username", content);
//                        passBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(passBack);
                    }
                });
            }
        });

        scanLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        lCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        lCodeScanner.releaseResources();
    }

    // this is for scanning QR code login
    // return false if user is not in the db, otherwise true.
//    public void checkIfUserExists(String username) {
//        DocumentReference docRef = db.collection("Users").document(username);
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        System.out.println("User Exists");
//                        writeFile(username);
//                        Intent passScan = new Intent(ScanLoginQR.this, ScanQR.class);
//                        Toast.makeText(getApplicationContext(), String.format("Login as '%s'", content), Toast.LENGTH_LONG).show();
//                        passScan.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(passScan);
//                    } else {
//                        System.out.println("User does not exist");
//                        Intent passLogin = new Intent(ScanLoginQR.this, LoginPage.class);
//                        Toast.makeText(getApplicationContext(), String.format("'%s' has not been created before, please use the login page", content), Toast.LENGTH_LONG).show();
//                        passLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(passLogin);
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });
//    }

    public void checkIfAdmin(String username) {
        DocumentReference docRef = db.collection("Admin").document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        System.out.println("Admin Exists");
                        writeFile(username);
                        Intent admin_page = new Intent(ScanLoginQR.this, AdminPage.class);
                        admin_page.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(admin_page);
                    } else {
                        System.out.println("Admin does not exist");

                        DocumentReference docRef = db.collection("Users").document(username);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        System.out.println("User Exists");
                                        writeFile(username);
                                        Intent passScan = new Intent(ScanLoginQR.this, ScanQR.class);
                                        Toast.makeText(getApplicationContext(), String.format("Login as '%s'", content), Toast.LENGTH_LONG).show();
                                        passScan.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(passScan);
                                    } else {
                                        System.out.println("User does not exist");
                                        Intent passLogin = new Intent(ScanLoginQR.this, LoginPage.class);
                                        Toast.makeText(getApplicationContext(), String.format("'%s' has not been created before, please use the login page", content), Toast.LENGTH_LONG).show();
                                        passLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(passLogin);
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void writeFile(String username) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.apply();
    }
}
