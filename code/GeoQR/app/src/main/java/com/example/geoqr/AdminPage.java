package com.example.geoqr;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminPage extends AppCompatActivity {
    private int selectedBtnId;
    private EditText searchBar;

    // Buttons
    private Button playerBtn;
    private Button qrCodeBtn;

    // Tables
    private TableLayout playerTable;
    private TableLayout qrCodeTable;

    // DB
    FirebaseFirestore db;
    CollectionReference qrCodeColRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Set Buttons & Tables
        playerBtn = findViewById(R.id.admin_select_players);
        qrCodeBtn = findViewById(R.id.admin_select_qr_codes);

        playerTable = findViewById(R.id.admin_player_table);
        qrCodeTable = findViewById(R.id.admin_qr_table);

        searchBar = findViewById(R.id.admin_search);

        // Set default state
        selectedBtnId = R.id.admin_select_players;

        // Set onClick Event Listeners
        playerBtn.setOnClickListener(this::toggle);
        qrCodeBtn.setOnClickListener(this::toggle);

        // Get List of QR codes
        db = FirebaseFirestore.getInstance();
        db.collection("QR codes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                    }

                    Log.d("QRs: ", list.toString());
                } else {
                    Log.d("Error:", "Error getting documents: ", task.getException());
                }
            }
        });
        // Get List of Players
        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                    }

                    Log.d("QRs: ", list.toString());
                } else {
                    Log.d("Error:", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void toggle(View btn) {
        if (btn.getId() != selectedBtnId) {
            setFocus(selectedBtnId, btn.getId());
        }
    }

    private void setFocus(int unfocus, int focus) {
        // Colors
        int focusedColor = ResourcesCompat.getColor(getResources(), R.color.btn_focused, null);
        int unFocusedColor = ResourcesCompat.getColor(getResources(), R.color.btn_unfocused, null);

        if (unfocus == playerBtn.getId()) {
            playerBtn.setBackgroundColor(unFocusedColor);
            qrCodeBtn.setBackgroundColor(focusedColor);

            playerTable.setVisibility(View.GONE);
            qrCodeTable.setVisibility(View.VISIBLE);

            searchBar.setHint("Search QR Codes");
        }

        else {
            qrCodeBtn.setBackgroundColor(unFocusedColor);
            playerBtn.setBackgroundColor(focusedColor);

            qrCodeTable.setVisibility(View.GONE);
            playerBtn.setVisibility(View.VISIBLE);

            searchBar.setHint("Search Players");
        }

        selectedBtnId = focus;
    }
}
