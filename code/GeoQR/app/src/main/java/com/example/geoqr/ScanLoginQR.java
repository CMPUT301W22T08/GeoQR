package com.example.geoqr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_login);
        Objects.requireNonNull(getSupportActionBar()).hide();

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
                        passContent();
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

    /**
     * Login
     */
    private void passContent() {
        Intent passName = new Intent(ScanLoginQR.this, ScanQR.class);
        Toast.makeText(getApplicationContext(), String.format("Login as '%s'", content), Toast.LENGTH_LONG).show();

        // to be written the checking method by using the public checkIfUserExists and checkIfAdmin
        // if passes, write db.
        passName.putExtra("username", content);
        startActivity(passName);
    }
}
