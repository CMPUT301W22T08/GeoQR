package com.example.geoqr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.Result;

// this class is to be implemented
public class ScanLoginQR extends AppCompatActivity {

    private CodeScanner CodeScanner;
    private static final int CAMERA_PERMISSION_CODE = 10;
    private String content;
    private CodeScannerView scanLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_login);

        scanLogin = findViewById(R.id.login_view);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, CAMERA_PERMISSION_CODE);
        }
        else {
            scanLogin();
        }
    }

    private void scanLogin() {
        CodeScanner = new CodeScanner(this, scanLogin);
        CodeScanner.setAutoFocusEnabled(true);
        CodeScanner.setFormats(CodeScanner.ALL_FORMATS);
        CodeScanner.setScanMode(ScanMode.CONTINUOUS);
        CodeScanner.setFlashEnabled(false);

        CodeScanner.setDecodeCallback(new DecodeCallback() {
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
                CodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CodeScanner.releaseResources();
    }

    private void passContent() {
        Intent passName = new Intent(ScanLoginQR.this, MainActivity.class);
        passName.putExtra("content", content);
        startActivity(passName);
    }

//    public boolean camPermission(Context context, String ... permissions) {
//        if (context != null && permissions != null) {
//            for (String permission : permissions) {
//                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

    // to be tested
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_CAMERA_PERMISSION) {
//            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Camera Permission needed", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        }
//    }
}
