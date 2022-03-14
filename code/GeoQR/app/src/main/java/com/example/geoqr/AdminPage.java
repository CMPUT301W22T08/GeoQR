package com.example.geoqr;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

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

public class AdminPage extends AppCompatActivity {
    private int selectedBtnId;
    private boolean deleteBtnDisabled;
    private EditText searchBar;

    // Buttons
    private Button playerBtn;
    private Button qrCodeBtn;
    private Button deleteBtn;

    // Headers
    private LinearLayout playerHeader;
    private LinearLayout qrCodeHeader;

    // ListViews
    private ListView playerList;
    private ListView qrCodeList;

    // Adapters
    private AdminPlayerAdapter playerAdapter;
    private AdminQRAdapter qrAdapter;

    // DB
    FirebaseFirestore db;

    // Selection
    ArrayList<AdminPlayerTuple> playerSelection = new ArrayList<>();
    ArrayList<AdminQRTuple> qrSelection = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Adapters
        playerAdapter = new AdminPlayerAdapter(this, new ArrayList<>());
        qrAdapter = new AdminQRAdapter(this, new ArrayList<>());

        // Set Buttons
        playerBtn = findViewById(R.id.admin_select_players);
        qrCodeBtn = findViewById(R.id.admin_select_qr_codes);
        deleteBtn = findViewById(R.id.admin_delete_btn);

        // Set Headers
        playerHeader = findViewById(R.id.admin_player_header);
        qrCodeHeader = findViewById(R.id.admin_qr_code_header);

        // Set ListViews
        playerList = findViewById(R.id.admin_player_list);

        qrCodeList = findViewById(R.id.admin_qr_code_list);

        // Search Bar
        searchBar = findViewById(R.id.admin_search);

        // Set default state
        selectedBtnId = R.id.admin_select_players;
        deleteBtnDisabled = true;

        // Set onClick Event Listeners
        playerBtn.setOnClickListener(this::toggle);
        qrCodeBtn.setOnClickListener(this::toggle);

        // Get List of QR codes
        db = FirebaseFirestore.getInstance();
        db.collection("QR codes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("RR: ", document.toString());
                        qrAdapter.add(new AdminQRTuple(document.getId(), (String) ((ArrayList) document.get("User")).get(0),
                                Math.toIntExact((Long) document.get("Score"))));
                    }
                }
            }
        });

        // Get List of Players
        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("GG: ", document.get("Score").toString());
                        playerAdapter.add(new AdminPlayerTuple(document.getId(), Math.toIntExact((Long) document.get("Score"))));
                    }
                } else {
                    Log.d("Error:", "Error getting documents: ", task.getException());
                }
            }
        });

        qrCodeList.setAdapter(qrAdapter);
        playerList.setAdapter(playerAdapter);

        // Set Listeners
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!deleteBtnDisabled) {
                    if (selectedBtnId == playerBtn.getId()) {
                        // Delete Players
                        deletePlayers();
                    }
                    else {
                        // Delete QR Codes
                        deleteQRCodes();
                    }
                }
            }
        });
        playerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                if (!playerSelection.contains(playerAdapter.getItem(pos))) {
                    // Highlight
                    view.setBackgroundColor(getResources().getColor(R.color.highlight, null));

                    playerSelection.add(playerAdapter.getItem(pos));
                }
                else {
                    // Unhighlight
                    view.setBackgroundColor(getResources().getColor(R.color.unhighlight, null));

                    playerSelection.remove(playerAdapter.getItem(pos));
                }

                if (playerSelection.size() != 0) {
                    deleteBtnDisabled = false;
                    deleteBtn.setBackgroundColor(getResources().getColor(R.color.deleteBtnEnabled, null));
                }
                else {
                    deleteBtnDisabled = true;
                    deleteBtn.setBackgroundColor(getResources().getColor(R.color.disabled, null));
                }
            }
        });
        qrCodeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                if(!qrSelection.contains(qrAdapter.getItem(pos))) {
                    // Highlight
                    view.setBackgroundColor(getResources().getColor(R.color.highlight, null));

                    qrSelection.add(qrAdapter.getItem(pos));
                }
                else {
                    // Unhighlight
                    view.setBackgroundColor(getResources().getColor(R.color.unhighlight, null));

                    qrSelection.remove(qrAdapter.getItem(pos));
                }

                if (qrSelection.size() != 0) {
                    deleteBtnDisabled = false;
                    deleteBtn.setBackgroundColor(getResources().getColor(R.color.deleteBtnEnabled, null));
                }
                else {
                    deleteBtnDisabled = true;
                    deleteBtn.setBackgroundColor(getResources().getColor(R.color.disabled, null));
                }
            }
        });
    }

    private void deleteQRCodes() {
        for (AdminQRTuple qrTuple: qrSelection) {
            db.collection("QR codes").document(qrTuple.getContents()).delete();
            db.collection("User")
                    .document(qrTuple.getPlayer())
                    .collection("QR codes").document(qrTuple.getContents())
                    .delete();
            qrAdapter.remove(qrTuple);
        }

        qrSelection.clear();
    }

    private void deletePlayers() {
        for (AdminPlayerTuple playerTuple: playerSelection) {
            db.collection("Users").document(playerTuple.getName()).delete();
            playerAdapter.remove(playerTuple);
        }

        playerSelection.clear();
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

            playerHeader.setVisibility(View.GONE);
            qrCodeHeader.setVisibility(View.VISIBLE);

            playerList.setVisibility(View.GONE);
            qrCodeList.setVisibility(View.VISIBLE);

            searchBar.setHint("Search QR Codes");

            // Unselect
            for (AdminPlayerTuple p: playerSelection) {
                playerList.getChildAt(playerAdapter.getPosition(p))
                    .setBackgroundColor(getResources().getColor(R.color.unhighlight, null));
            }

            playerSelection.clear();
        }

        else {
            qrCodeBtn.setBackgroundColor(unFocusedColor);
            playerBtn.setBackgroundColor(focusedColor);

            qrCodeHeader.setVisibility(View.GONE);
            playerHeader.setVisibility(View.VISIBLE);

            qrCodeList.setVisibility(View.GONE);
            playerList.setVisibility(View.VISIBLE);

            searchBar.setHint("Search Players");

            // Unselect
            for (AdminQRTuple q: qrSelection) {
                qrCodeList.getChildAt(qrAdapter.getPosition(q))
                    .setBackgroundColor(getResources().getColor(R.color.unhighlight, null));
            }

            qrSelection.clear();
        }

        selectedBtnId = focus;

        deleteBtnDisabled = true;
        deleteBtn.setBackgroundColor(getResources().getColor(R.color.disabled, null));
    }
}
