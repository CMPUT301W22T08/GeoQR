package com.example.geoqr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

public class ScanStatusQR extends AppCompatActivity {

    private CodeScanner sCodeScanner;
    private String content;
    private CodeScannerView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_status);
        Objects.requireNonNull(getSupportActionBar()).hide();
        status = findViewById(R.id.status_view);

        // check permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getApplicationContext(), "Camera Permission Needed", Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            scanStatus();
        }
    }

    /**
     * provides camera to scan the user's QR code
     */
    private void scanStatus() {
        sCodeScanner = new CodeScanner(this, status);
        sCodeScanner.setAutoFocusEnabled(true);
        sCodeScanner.setFormats(CodeScanner.ALL_FORMATS);
        sCodeScanner.setScanMode(ScanMode.CONTINUOUS);
        sCodeScanner.setFlashEnabled(false);

        sCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // pass content back to the login page for authentication
                        content = result.getText();
                        Intent passBack = new Intent(ScanStatusQR.this, ScoreboardActivity.class);
                        passBack.putExtra("username", content);
                        passBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        setResult(20, passBack);
                        ScanStatusQR.super.onBackPressed();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sCodeScanner.releaseResources();
    }
}
