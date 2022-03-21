package com.example.geoqr;

import static android.content.ContentValues.TAG;

import android.Manifest;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginPage extends AppCompatActivity {

    private EditText etUsername;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String username_scan, username;
    // private CollectionReference ref = db.collection("Users");

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        if (!checkCamera(this)) {
            Toast.makeText(getApplicationContext(), "You need a camera for this app", Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            checkPermissions();
        }

        db = FirebaseFirestore.getInstance();

        etUsername = findViewById(R.id.et_Username);
        Button btnGenerate = findViewById(R.id.btn_Generate);
        Button btnLogin = findViewById(R.id.btn_Login);
        FloatingActionButton scanLogin = findViewById(R.id.scan_login);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = etUsername.getText().toString();

                if (!TextUtils.isEmpty(username)) {  // if the username is not empty
                    Toast.makeText(getApplicationContext(), String.format("Login as '%s'", username), Toast.LENGTH_LONG).show();
                    writeFile(username);
                    openScan(username);
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

        // scanning login button
        scanLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scan = new Intent(LoginPage.this, ScanLoginQR.class);
                // scan.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(scan);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    public void add_user_detail() {
        HashMap<String, Object> user_detail = new HashMap<>();
        user_detail.put("Contact", "null");
        user_detail.put("Total Score", "0");
        user_detail.put("Highest Score", "0");
        user_detail.put("Lowest Score", "0");
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



    private void openScan(String username) {
        boolean checkUser = checkIfUserExists(username);
        boolean checkAdmin = checkIfAdmin(username);
        if (!checkAdmin) {
            Intent camScan = new Intent(LoginPage.this, ScanQR.class);
            // if (checkUser == false)
            if (!checkUser) {
                add_user_detail(); // we need to check if the user exists, or we do not have to proceed this line
            }
            Toast.makeText(getApplicationContext(), String.format("Login as '%s'", username), Toast.LENGTH_LONG).show();
            camScan.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(camScan);
        }
        else {
            Intent admin_page = new Intent(this, AdminPage.class);
            admin_page.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(admin_page);
        }
    }

    // check if the user is admin
    public boolean checkIfAdmin(String username) {
        return false;
    }

    // this is for scanning QR code login
    // return false if user is not in the db, otherwise true.
    public boolean checkIfUserExists(String username) {
        return false;
    }


    private void generate(){
        RandomString randomString = new RandomString();
        String result = randomString.generateAlphaNumeric(12);
        etUsername.setText(result);
    }

    public void writeFile(String username) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.apply();
    }

    private void load() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        String get_user = sharedPreferences.getString("username", null);
        System.out.println(get_user);
        if (get_user != null) {
            username = get_user;
            Intent camScan = new Intent(LoginPage.this, ScanQR.class);
            System.out.println(username);
            Toast.makeText(getApplicationContext(), String.format("Login as '%s'", username), Toast.LENGTH_LONG).show();
            camScan.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(camScan);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }


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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            for (int index = permissions.length - 1; index >= 0; --index) {
                if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                    System.out.println(permissions[index]);

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
            load();
        }
    }

}
