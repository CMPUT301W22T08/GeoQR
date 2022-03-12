package com.example.geoqr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.Result;

// CLASS TO BE TESTED
public class Camera_test extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    private static final int REQUEST_CAMERA_PERMISSION = 0;
    public String content;
    private CodeScannerView scannerView;
    Bitmap bitmap;

    float x1, x2, y1, y2;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_v2);

        scannerView = findViewById(R.id.scan_view);
        // LinearLayout cameraLayout = findViewById(R.id.camera_layout);
//        int permission_all = 1;
//        String[] permissions = {
//                Manifest.permission.CAMERA
//        };
//
//        if (!camPermission(this, permissions)) {
//            ActivityCompat.requestPermissions(this, permissions, permission_all);
//        }
//        else {
//            scanCode();
//        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] {
                    Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return;
        }
        else {
            scanCode();
        }

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
                            Intent left = new Intent(Camera_test.this, MainActivity.class);
                            startActivity(left);
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        }
                        else if (x1 > x2) {
                            Intent right = new Intent(Camera_test.this, MainActivity.class);
                            startActivity(right);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void scanCode() {
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setAutoFocusEnabled(true);
        mCodeScanner.setFormats(CodeScanner.ALL_FORMATS);
        mCodeScanner.setScanMode(ScanMode.CONTINUOUS);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        content = result.getText();
                        calculateScore();
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

    private void takeScreenShot() {
        try {
            View view = getWindow().getDecorView().getRootView();
            view.setDrawingCacheEnabled(true);
            bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }


    public boolean camPermission(Context context, String ... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /*
    // to be tested
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera Permission needed", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

     */

    public boolean checkCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private void calculateScore() {
        Intent calScore = new Intent(Camera_test.this, addQR.class);
        takeScreenShot();
        Bundle bundle = new Bundle();
        bundle.putParcelable("bitmap", bitmap);
        bundle.putString("content", content);
        // calScore.putExtra("content", content);
        // calScore.putExtra("bitmap", bitmap);
        calScore.putExtras(bundle);
        startActivity(calScore);
    }
}
