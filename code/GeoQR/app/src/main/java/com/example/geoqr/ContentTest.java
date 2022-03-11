package com.example.geoqr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ContentTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_content);

        Intent intent = getIntent();
        String content = intent.getStringExtra("content");

        TextView show = findViewById(R.id.test_text);
        show.setText(content);
    }
}
