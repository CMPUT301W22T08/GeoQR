package com.example.geoqr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.budiyev.android.codescanner.*;
import com.google.zxing.Result;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// CLASS TO BE TESTED
public class Camera_test extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    private static final int REQUEST_CAMERA_PERMISSION = 0;
    public String content;
    private CodeScannerView scannerView;
    float x1, x2, y1, y2;
    int width = 480;
    int height = 480;
    byte[] bytes;

    private TextureView cam;

    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSessions;
    private CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private Handler bgHandler;
    private HandlerThread bgThread;

    /**
     *
     */

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0,90);
        ORIENTATIONS.append(Surface.ROTATION_90,0);
        ORIENTATIONS.append(Surface.ROTATION_180,270);
        ORIENTATIONS.append(Surface.ROTATION_270,180);
    }

    CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
        }
    };
    /**
     *
     */

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        setContentView(R.layout.camera_v2);




        cam = findViewById(R.id.cameraView);
        Button btn = findViewById(R.id.scan_btn);





        scannerView = findViewById(R.id.scan_view);
        RelativeLayout cameraLayout = findViewById(R.id.camera_layout);
        int permission_all = 1;
        String[] permissions = {
                Manifest.permission.CAMERA
        };

        if (!camPermission(this, permissions)) {
            Toast.makeText(this, "Camera Permission Needed", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, permissions, permission_all);
        }
        else {
            scanCode();
        }

        cameraLayout.setOnTouchListener(new View.OnTouchListener() {
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



    public boolean onTouchEvent(MotionEvent touchEvent) {

        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
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

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
        startBackgroundThread();
        if(cam.isAvailable())
            oCam();
        else
            cam.setSurfaceTextureListener(textureListener);
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        stopBackgroundThread();
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


    private void scanPicture() {
        if (cameraDevice == null) {
            return;
        }
        // CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            /*
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());

            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                        .getOutputSizes(ImageFormat.JPEG);
            }

            // Capture image with custom size


            if (jpegSizes != null && jpegSizes.length > 0) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
                System.out.println(width);
                System.out.println(height);
            }
             */

            final ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG,1);
            List<Surface> outputSurface = new ArrayList<>(2);
            outputSurface.add(reader.getSurface());
            outputSurface.add(new Surface(cam.getSurfaceTexture()));

            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            // Check orientation base on device
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION,ORIENTATIONS.get(rotation));

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader imageReader) {
                    try (Image image = reader.acquireLatestImage()) {
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        // passPic(bytes);
                    }
//                    catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
//                private void passPic(byte[] bytes) throws IOException {
//                    start(bytes);
//                }
            };

            reader.setOnImageAvailableListener(readerListener,bgHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    createCameraPreview();
                }
            };

            cameraDevice.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    try {
                        cameraCaptureSession.capture(captureBuilder.build(), captureListener, bgHandler);
                    }
                    catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed (@NonNull CameraCaptureSession cameraCaptureSession) {
                }
            }, bgHandler);


        }
        catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean checkCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private void calculateScore() {
        Intent calScore = new Intent(Camera_test.this, addQR.class);
        scanPicture();
        calScore.putExtra("content", content);
        calScore.putExtra("bytes", bytes);
        startActivity(calScore);
    }

//    private void start(byte[] bytes) {
//        Intent score = new Intent(Camera_test.this, addQR.class);
//        score.putExtra("image", bytes);
//        startActivity(score);
//    }

    private void createCameraPreview() {
        try {
            SurfaceTexture texture = cam.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(),imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            // Arrays.asList(surface)
            cameraDevice.createCaptureSession(Collections.singletonList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if(cameraDevice == null)
                        return;
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(Camera_test.this, "Changed", Toast.LENGTH_SHORT).show();
                }
            },null);
        }
        catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if (cameraDevice == null) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(),null,bgHandler);
        }
        catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void oCam() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(manager.getCameraIdList()[0]);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            // Check the permission see if camera is granted
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[] {
                        Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(manager.getCameraIdList()[0], stateCallback,null);
        }
        catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            oCam();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }
    };

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

    private void stopBackgroundThread() {
        bgThread.quitSafely();
        try{
            bgThread.join();
            bgThread= null;
            bgHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        bgThread = new HandlerThread("Camera Background");
        bgThread.start();
        bgHandler = new Handler(bgThread.getLooper());
    }
}
