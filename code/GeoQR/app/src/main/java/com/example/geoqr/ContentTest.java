package com.example.geoqr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
        // byte[] bytes = bundle.getByteArray("bytes");

        TextView show = findViewById(R.id.test_text);
        show.setText(content);

        // Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        ImageView show_b = findViewById(R.id.bit_test);
        // show_b.setImageBitmap(bitmap);
    }
}
