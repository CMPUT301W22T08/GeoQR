package com.example.geoqr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;

// CLASS TO BE TESTED
public class Camera_V2 extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    // private static final int REQUEST_CAMERA_PERMISSION = 0;
    private static final int CAMERA_PERMISSION_CODE = 10;
    public String content;
    private CodeScannerView scannerView;
    float x1, x2, y1, y2;
    Bitmap bitmap, btm;
    byte[] bytes, byte_test;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_v2);

        scannerView = findViewById(R.id.scan_view);
        FloatingActionButton profile_btn = findViewById(R.id.profile_btn);
        // int permission_all = 1;

//        String[] permissions = {
//                Manifest.permission.CAMERA
//        };

//        int check = 0;
//        for (int i = 0; ; i++ ) {
//            if (check == 3) {
//                Toast.makeText(this, "Camera Permission Needed", Toast.LENGTH_LONG).show();
//                finish();
//                break;
//            }
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
//                check++;
//                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, CAMERA_PERMISSION_CODE);
//            }
//            else {
//                scanCode();
//                break;
//            }
//        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, CAMERA_PERMISSION_CODE);
        }
        else {
            scanCode();
        }

        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(Camera_V2.this, MainActivity.class);
                startActivity(profile);
            }
        });

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
                            Intent left = new Intent(Camera_V2.this, MainActivity.class);
                            startActivity(left);
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        }
                        else if (x1 > x2) {
                            Intent right = new Intent(Camera_V2.this, MainActivity.class);
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
        mCodeScanner.setFlashEnabled(false);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        IntentIntegrator intentIntegrator = new IntentIntegrator(Camera_V2.this);
                        intentIntegrator.setBarcodeImageEnabled(true);
                        intentIntegrator.initiateScan();

                        content = result.getText();

                        // to be tested
                        screenShot();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        bytes = stream.toByteArray();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            btm = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            btm.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte_test = stream.toByteArray();
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void screenShot() {
        mCodeScanner.stopPreview();
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
        super.onPause();
        mCodeScanner.releaseResources();
    }

    private void calculateScore() {
        Intent calScore = new Intent(Camera_V2.this, ContentTest.class);
        Bundle bundle = new Bundle();
        //bundle.putParcelable("bitmap", bitmap);
        bundle.putString("content", content);
        bundle.putByteArray("bytes", byte_test);
        //calScore.putExtra("content", content);
        calScore.putExtras(bundle);
        startActivity(calScore);
    }

    public boolean checkCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
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
