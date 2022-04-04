package com.example.geoqr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
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

/**
 * This class provides the camera to scan the QR codes
 */
public class ScanQR extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    public String content;
    private CodeScannerView scannerView;
    float x1, x2, y1, y2;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    int check_dialog;
    String username;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        Objects.requireNonNull(getSupportActionBar()).hide();

        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        scannerView = findViewById(R.id.login_view);
        FloatingActionButton profile_btn = findViewById(R.id.profile_btn);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getApplicationContext(), "Camera Permission Needed", Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            scanCode();
        }

        // profile button pressed
        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(ScanQR.this, ProfilePage.class);
                startActivity(profile);
            }
        });

        // swipe view
        scannerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = motionEvent.getX();
                        y1 = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = motionEvent.getX();
                        y2 = motionEvent.getY();
                        if (x1 < x2) {
                            Intent left = new Intent(ScanQR.this, MapActivity.class);
                            left.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(left);
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        }
                        else if (x1 > x2) {
                            Intent right = new Intent(ScanQR.this, ScoreboardActivity.class);
                            right.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(right);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                        break;
                }
                return false;
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                if (check_dialog == 0) { // to be implemented as the show alert dialog
                    check_dialog = 1;
                    AlertDialog.Builder alert = new AlertDialog.Builder(ScanQR.this);
                    AlertDialog alertDialog = alert.create();
                    if (!alertDialog.isShowing()) {
                        alert.setTitle("Logout Confirmation");
                        alert.setMessage(String.format("Are you sure you want to Logout '%s'?", username));
                        alert.setPositiveButton(android.R.string.yes, (dialogInterface, i1) -> {
                            Intent log_page = new Intent(ScanQR.this, LoginPage_V2.class);
                            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            check_dialog = 0;
                            editor.remove("username");
                            // editor.clear();
                            editor.apply();
                            Toast.makeText(getApplicationContext(), String.format("%s has been logged out", username), Toast.LENGTH_LONG).show();
                            log_page.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(log_page);
                        });
                        alert.setNegativeButton(android.R.string.no, (dialogInterface, i1) -> {
                            dialogInterface.cancel();
                            check_dialog = 0;
                        });
                        alert.show();
                    }
                }
            }
        });
    }

    /**
     * start the preview
     */
    private void scanCode() {
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setAutoFocusEnabled(true);
        mCodeScanner.setFormats(CodeScanner.ALL_FORMATS);
        mCodeScanner.setScanMode(ScanMode.CONTINUOUS);
        mCodeScanner.setFlashEnabled(false);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        content = result.getText();
                        passScore();
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
        mCodeScanner.releaseResources();
    }

    /**
     * passing the content to the addQR class and show summary
     */
    private void passScore() {
        Intent calScore = new Intent(ScanQR.this, addQR.class);
        calScore.putExtra("content", content);
        startActivity(calScore);
    }
}
