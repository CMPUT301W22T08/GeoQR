package com.example.geoqr;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

/**
 * The View class for the Admin page.
 */
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

    // Admin Instance
    private Admin admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        admin = new Admin((Context) this);
        admin.fetch();

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

        qrCodeList.setAdapter(admin.getQRAdapter());
        playerList.setAdapter(admin.getPlayerAdapter());

        // Set Listeners
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!deleteBtnDisabled) {
                    if (selectedBtnId == playerBtn.getId()) {
                        // Delete Players
                        admin.deletePlayers();
                    }
                    else {
                        // Delete QR Codes
                        admin.deleteQRCodes();
                    }

                    deleteBtnDisabled = true;
                    deleteBtn.setBackgroundColor(getResources().getColor(R.color.disabled, null));
                }
            }
        });

        playerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                if (!admin.playerSelectedAt(pos)) {
                    // Highlight
                    view.setBackgroundColor(getResources().getColor(R.color.highlight, null));

                    admin.addSelectedPlayerAt(pos);
                }
                else {
                    // Unhighlight
                    view.setBackgroundColor(getResources().getColor(R.color.unhighlight, null));

                    admin.removeSelectedPlayerAt(pos);
                }

                if (!admin.noPlayerSelected()) {
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
                if(!admin.qrSelectedAt(pos)) {
                    // Highlight
                    view.setBackgroundColor(getResources().getColor(R.color.highlight, null));

                    admin.addSelectedQRAt(pos);
                }
                else {
                    // Unhighlight
                    view.setBackgroundColor(getResources().getColor(R.color.unhighlight, null));

                    admin.removeSelectedQRAt(pos);
                }

                if (!admin.noQRSelected()) {
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
            for (Integer pos: admin.getSelectedPlayerIndices()) {
                playerList.getChildAt(pos)
                        .setBackgroundColor(getResources().getColor(R.color.unhighlight, null));
            }

            admin.resetPlayerSelection();
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
            for (Integer pos: admin.getSelectedQRIndices()) {
                qrCodeList.getChildAt(pos)
                        .setBackgroundColor(getResources().getColor(R.color.unhighlight, null));
            }

            admin.resetQRSelection();
        }

        selectedBtnId = focus;

        deleteBtnDisabled = true;
        deleteBtn.setBackgroundColor(getResources().getColor(R.color.disabled, null));
    }
}
