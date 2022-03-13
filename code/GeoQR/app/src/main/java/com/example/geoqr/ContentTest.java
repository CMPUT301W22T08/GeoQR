package com.example.geoqr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ContentTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_content);

        Intent intent = getIntent();
        //String content = intent.getStringExtra("content");
        Bundle bundle = intent.getExtras();

        String content = bundle.getString("content");
        Bitmap bitmap = bundle.getParcelable("bitmap");

        TextView show = findViewById(R.id.test_text);
        show.setText(content);
        ImageView show_b = findViewById(R.id.bit_test);
        show_b.setImageBitmap(bitmap);
    }
}
