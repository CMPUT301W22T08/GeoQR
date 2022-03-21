package com.example.geoqr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileDetails extends AppCompatActivity {

    TextView detail_content, detail_score, detail_date, detail_loc, detail_comment;
    ImageView detail_img;
    FirebaseFirestore db;
    Button detail_edit, detail_o, detail_x;
    EditText detail_edit_bar;
    String new_comment;
    private String[] list;

    @Override
    protected void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        setContentView(R.layout.qr_detail);
        Intent intent = getIntent();

        db = FirebaseFirestore.getInstance();
        detail_content = findViewById(R.id.detail_content);
        detail_comment = findViewById(R.id.detail_comment);
        detail_score = findViewById(R.id.detail_score);
        detail_date = findViewById(R.id.detail_date);
        detail_loc = findViewById(R.id.detail_loc);
        detail_img = findViewById(R.id.detail_img);
        detail_edit = findViewById(R.id.detail_edit);
        detail_o = findViewById(R.id.detail_o);
        detail_x = findViewById(R.id.detail_x);
        detail_edit_bar = findViewById(R.id.detail_edit_bar);
        FloatingActionButton back_btn = findViewById(R.id.detail_back);
        FloatingActionButton del_btn = findViewById(R.id.detail_del);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(ProfileDetails.this, ProfilePage.class);
                back.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(back);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//
//
//                String str = intent.getStringExtra("item");
//                int pos = Integer.parseInt(str);
//

//                list = intent.getArra("array");
////                ArrayList<String> newList = new ArrayList<String>(Arrays.asList(list));
//                ArrayList<String> newList = (ArrayList<String>) intent.getSerializableExtra("array");
//                newList.remove(pos);


            }
        });

        detail_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detail_comment.setVisibility(View.GONE);
                detail_edit_bar.setText(detail_comment.getText().toString());
                detail_edit_bar.setVisibility(View.VISIBLE);
                detail_edit.setVisibility(View.GONE);
                detail_o.setVisibility(View.VISIBLE);
                detail_x.setVisibility(View.VISIBLE);
                detail_edit_bar.requestFocus();
                showKeyboard(ProfileDetails.this);
                detail_o.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new_comment = detail_edit_bar.getText().toString();
                        detail_edit_bar.setVisibility(View.GONE);
                        detail_comment.setText(new_comment);
                        detail_comment.setVisibility(View.VISIBLE);
                        hideKeyboard(ProfileDetails.this);
                    }
                });

                detail_x.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        detail_edit_bar.setVisibility(View.GONE);
                        detail_comment.setVisibility(View.VISIBLE);
                        hideKeyboard(ProfileDetails.this);
                    }
                });
            }
        });


    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void showKeyboard(Activity activity) {
        if (activity.getCurrentFocus().requestFocus()) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.showSoftInput(activity.getCurrentFocus(), InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
