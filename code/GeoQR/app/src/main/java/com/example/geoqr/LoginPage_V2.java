package com.example.geoqr;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class LoginPage_V2 extends AppCompatActivity {

    private EditText etUsername;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String username;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION};
    String unique; // this device's unique ID
    Boolean flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        Objects.requireNonNull(getSupportActionBar()).hide();
        // check if there is a camera on this device
        if (!checkCamera(this)) {
            Toast.makeText(getApplicationContext(), "You need a camera for this app", Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            checkPermissions();
        }

        db = FirebaseFirestore.getInstance();
        Button btnGenerate = findViewById(R.id.btn_Generate);
        Button btnLogin = findViewById(R.id.btn_Login);
        FloatingActionButton scanLogin = findViewById(R.id.scan_login);

        // login button pressed
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = etUsername.getText().toString();

                if (!TextUtils.isEmpty(username)) {  // if the username is not empty
                    // Toast.makeText(getApplicationContext(), String.format("Login as '%s'", username), Toast.LENGTH_LONG).show();
                    flag = false;
                    checkIfAdmin(username);
                } else {
                    Toast.makeText(LoginPage_V2.this, "Username cannot be empty, please re-enter!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // auto generate button pressed
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generate();
            }
        });

        // scanning login button pressed
        scanLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to the scanLoginQR page (get result from activity)
                Intent scan = new Intent(LoginPage_V2.this, ScanLoginQR.class);
                activityResultLauncher.launch(scan);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    // get result from scanQRLogin
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 10) {
                        Intent content = result.getData();
                        if (content != null) {
                            username = content.getStringExtra("username");
                            etUsername.setText(username);
                            flag = true;
                            checkIfAdmin(username);
                        }
                        else {
                            System.out.println("Content is null from result");
                        }
                    }
                }
            }
    );

    // new user add details to the DB
    public void add_user_detail() {
        HashMap<String, Object> user_detail = new HashMap<>();
        user_detail.put("Contact", "null");
        user_detail.put("Total Score", "0");
        user_detail.put("Highest Score", "0");
        user_detail.put("Lowest Score", "0");
        user_detail.put("UUID", unique);
        db.collection("Users").document(username).set(user_detail);
    }

    /**
     * This class helps device to check if it does have camera (hardware)
     * @return boolean
     * Return False if there is no camera, true otherwise
     */
    private boolean checkCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    // check if the user is admin
    public void checkIfAdmin(String username) {
        DocumentReference docRef = db.collection("Admin").document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        System.out.println("Admin Exists");
                        Toast.makeText(getApplicationContext(), String.format("Login as '%s'", username), Toast.LENGTH_LONG).show();
                        Intent admin_page = new Intent(LoginPage_V2.this, AdminPage.class);
                        admin_page.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(admin_page);
                    } else {
                        System.out.println("Not Admin");
                        checkIfUserExists(username);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    // this is for scanning QR code login
    // return false if user is not in the db, otherwise true.
    public void checkIfUserExists(String username) {
        DocumentReference docRef = db.collection("Users").document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        db.collection("Users").document(username).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String check_unique = documentSnapshot.getString("UUID");
                                        assert check_unique != null;
                                        // if this user logs in in the different device
                                        if (!check_unique.equals(unique)) {
                                            AlertDialog.Builder alert = new AlertDialog.Builder(LoginPage_V2.this);

                                            if (flag) {
                                                alert.setTitle("Login Confirmation");
                                                alert.setMessage("You account has logged in in another device, are you sure you want to change to this device?");
                                                alert.setPositiveButton(android.R.string.yes, (dialogInterface, i1) -> {
                                                    System.out.println("user exists");
                                                    db.collection("Users").document(username).update("UUID", unique);
                                                    Intent camScan = new Intent(LoginPage_V2.this, ScanQR.class);
                                                    Toast.makeText(getApplicationContext(), String.format("Login as '%s'", username), Toast.LENGTH_LONG).show();
                                                    camScan.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(camScan);
                                                });
                                                alert.setNegativeButton(android.R.string.no, (dialogInterface, i1) -> {
                                                    dialogInterface.cancel();
                                                });
                                                alert.show();
                                            }
                                            else {
                                                // alert
                                                alert.setTitle("Login Error");
                                                alert.setMessage("You account has logged in in another device, please use scanning to login.");
                                                alert.setPositiveButton(android.R.string.ok, (dialogInterface, i1) -> {
                                                    dialogInterface.cancel();
                                                });
                                                alert.show();
                                            }
                                        }
                                        else {
                                            System.out.println("user exists");
                                            Intent camScan = new Intent(LoginPage_V2.this, ScanQR.class);
                                            Toast.makeText(getApplicationContext(), String.format("Login as '%s'", username), Toast.LENGTH_LONG).show();
                                            camScan.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(camScan);
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Debug", "Cannot get that");
                                    }
                                });
                    }
                    else {
                        System.out.println("user does not exist");
                        add_user_detail();
                        writeFile(username, unique);
                        Intent camScan = new Intent(LoginPage_V2.this, ScanQR.class);
                        Toast.makeText(getApplicationContext(), String.format("Login as '%s'", username), Toast.LENGTH_LONG).show();
                        camScan.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(camScan);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    // generate random string
    private void generate(){
        RandomString randomString = new RandomString();
        String result = randomString.generateAlphaNumeric(12);
        etUsername.setText(result);
    }

    // write file (for auto login)
    private void writeFile(String username, String unique) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("UUID", unique);
        editor.apply();
    }

    // load when the application starts (auto login purpose)
    private void load() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        String get_user = sharedPreferences.getString("username", null);
        String get_unique = sharedPreferences.getString("UUID", null);
        if (get_user != null && get_unique != null) {
            username = get_user;
            unique = get_unique;
            etUsername.setText(username);
            checkIfAdmin(username);
        }
    }

    // check all permissions (camera, location)
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[0]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    // request permissions (camera, location)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            for (int index = permissions.length - 1; index >= 0; --index) {
                if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {

                    if (permissions[index].equals("android.permission.ACCESS_FINE_LOCATION")) {
                        Toast.makeText(this, "Location Permission Needed", Toast.LENGTH_LONG).show();
                    }
                    if (permissions[index].equals("android.permission.CAMERA")) {
                        Toast.makeText(this, "Camera Permission Needed", Toast.LENGTH_LONG).show();
                    }
                    // exit the app if one permission is not granted
                    finish();
                    return;
                }
            }
            etUsername = findViewById(R.id.et_Username);
            unique = UUID.randomUUID().toString();
            load(); // load after asking the permission or checking the permission (auto login purpose)
        }
    }
}
