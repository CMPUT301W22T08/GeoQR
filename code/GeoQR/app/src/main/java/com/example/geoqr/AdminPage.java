package com.example.geoqr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.util.Objects;

/**
 * The View class for the Admin page.
 */
public class AdminPage extends AppCompatActivity {
    private int selectedBtnId;
    private boolean deleteBtnDisabled;

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

    // Shake event detector
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    int check_dialog;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Objects.requireNonNull(getSupportActionBar()).hide();
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

        // Set default state
        selectedBtnId = R.id.admin_select_players;
        deleteBtnDisabled = true;

        // Set onClick Event Listeners
        playerBtn.setOnClickListener(this::toggle);
        qrCodeBtn.setOnClickListener(this::toggle);

        qrCodeList.setAdapter(admin.getQRAdapter());
        playerList.setAdapter(admin.getPlayerAdapter());

        
        //https://stackoverflow.com/questions/68895807/how-to-auto-refresh-data-in-android-studio-every-second
        final Handler handler = new Handler();
        Runnable refresh = new Runnable() {
            @Override
            public void run() {
                Log.d("Debug", "fetching");
                // data request
                admin.fetch();
                qrCodeList.setAdapter(admin.getQRAdapter());
                playerList.setAdapter(admin.getPlayerAdapter());
                handler.postDelayed(this, 10000);
            }
        };
        handler.postDelayed(refresh, 10000);


        // Set Listeners
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(AdminPage.this);
                alert.setTitle("Delete Confirmation");
                alert.setMessage("Are you sure you want to delete this item?");
                alert.setPositiveButton(android.R.string.yes, (dialogInterface, i1) -> {
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
                        deleteBtn.setEnabled(false);
                    }
                });
                alert.setNegativeButton(android.R.string.no, (dialogInterface, i1) -> {
                    dialogInterface.cancel();
                });
                alert.show();
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
                    deleteBtn.setEnabled(true);
                }
                else {
                    deleteBtnDisabled = true;
                    deleteBtn.setBackgroundColor(getResources().getColor(R.color.disabled, null));
                    deleteBtn.setEnabled(false);
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
                    deleteBtn.setEnabled(true);
                }
                else {
                    deleteBtnDisabled = true;
                    deleteBtn.setBackgroundColor(getResources().getColor(R.color.disabled, null));
                    deleteBtn.setEnabled(false);
                }
            }
        });

        // Shaking event
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                if (check_dialog == 0) { // to be implemented as the show alert dialog
                    check_dialog = 1;
                    AlertDialog.Builder alert = new AlertDialog.Builder(AdminPage.this);
                    AlertDialog alertDialog = alert.create();
                    if (!alertDialog.isShowing()) {
                        alert.setTitle("Logout Confirmation");
                        alert.setMessage(String.format("Are you sure you want to Logout '%s'?", username));
                        alert.setPositiveButton(android.R.string.yes, (dialogInterface, i1) -> {
                            Intent log_page = new Intent(AdminPage.this, LoginPage.class);
                            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            check_dialog = 0;
                            editor.clear();
                            editor.apply();
                            Toast.makeText(getApplicationContext(), String.format("%s has been logged out", username), Toast.LENGTH_LONG).show();
                            log_page.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(log_page);
                        });
                        alert.setNegativeButton(android.R.string.no, (dialogInterface, i1) -> {
                            dialogInterface.cancel();
                            check_dialog = 0;
                        });
                        alert.show();
                    }
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

            qrCodeList.setAdapter(admin.getQRAdapter());
            qrCodeList.setVisibility(View.VISIBLE);

            admin.resetPlayerSelection();
        }

        else {
            qrCodeBtn.setBackgroundColor(unFocusedColor);
            playerBtn.setBackgroundColor(focusedColor);

            qrCodeHeader.setVisibility(View.GONE);
            playerHeader.setVisibility(View.VISIBLE);

            qrCodeList.setVisibility(View.GONE);

            playerList.setVisibility(View.VISIBLE);
            playerList.setAdapter(admin.getPlayerAdapter());

            admin.resetQRSelection();
        }

        selectedBtnId = focus;

        deleteBtnDisabled = true;
        deleteBtn.setBackgroundColor(getResources().getColor(R.color.disabled, null));
        deleteBtn.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }
}
