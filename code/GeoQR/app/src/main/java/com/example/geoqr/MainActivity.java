package com.example.geoqr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // setContentView(R.layout.login_page);

        Camera_V2 cam = new Camera_V2();
        if (!cam.checkCamera(this)) {
            Toast.makeText(getApplicationContext(), "You do not have camera on this device", Toast.LENGTH_LONG).show();
            finish();
        }

        Button btn = findViewById(R.id.test_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Camera_test.class);
                startActivity(intent);
            }
        });
    }

}