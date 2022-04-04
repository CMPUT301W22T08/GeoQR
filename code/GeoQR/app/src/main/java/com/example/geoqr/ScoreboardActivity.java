package com.example.geoqr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

/**
 * The View class for Scoreboard Activity
 */
public class ScoreboardActivity extends AppCompatActivity implements Scoreboard.RankingUpdatable {

    final String[] scoreText = {"Total Score", "Highest QR Score", "Number of QRs"};

    // Model
    protected Scoreboard scoreboard;
    int board = 0;

    // View groups
    private TabLayout tabs;
    private ListView rankingView;
    private EditText searchBar;
    private Button backBtn;
    private Button scanBtn;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    int check_dialog;

    private ScoreboardRankingAdapter rankingAdapter;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Views
        rankingView = findViewById(R.id.scoreboard_ranking);
        tabs = findViewById(R.id.scoreboard_tabs);
        searchBar = findViewById(R.id.scoreboard_searchbar);
        scanBtn = findViewById(R.id.scoreboard_scan_status_qr);
        backBtn = findViewById(R.id.scoreboard_go_back);

        // Set back button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go back to ScanQR
                Intent intent = new Intent(ScoreboardActivity.this, ScanQR.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        // Navigate to ScanStatusQR activity on scanBtn click
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Goto the ScanStatusQR
                Intent scan = new Intent(ScoreboardActivity.this, ScanStatusQR.class);
                activityResultLauncher.launch(scan);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
            }
        });

        // Filter the users using the search bar
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence query, int i, int i1, int i2) {
                scoreboard.filterUsers(query.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Get player name
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        // Instantiate the model classes
        scoreboard = new Scoreboard(this, sharedPreferences.getString("username", null));
        rankingAdapter = new ScoreboardRankingAdapter((Context) this, scoreboard.getUsers());

        // Set adapters and currently visible ranking list
        rankingAdapter.setBoard(board);
        rankingView.setAdapter(rankingAdapter);

        // Open User profile on click on an item in the list
        rankingView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showUserProfile(rankingAdapter.getItem(i));
            }
        });

        // Change ranking list on the change of tabs
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                board = tab.getPosition();
                TextView score = findViewById(R.id.scoreboard_ranking_header_score);
                score.setText(scoreText[board]);

                rankingAdapter.setBoard(board);
                updatePlayerMetric();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // If the user shakes the device they are logged out
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                if (check_dialog == 0) { // to be implemented as the show alert dialog
                    check_dialog = 1;
                    AlertDialog.Builder alert = new AlertDialog.Builder(ScoreboardActivity.this);
                    AlertDialog alertDialog = alert.create();
                    if (!alertDialog.isShowing()) {
                        alert.setTitle("Logout Confirmation");
                        alert.setMessage(String.format("Are you sure you want to Logout '%s'?", sharedPreferences.getString("username", null)));
                        alert.setPositiveButton(android.R.string.yes, (dialogInterface, i1) -> {
                            Intent log_page = new Intent(ScoreboardActivity.this, LoginPage_V2.class);
                            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            check_dialog = 0;
                            editor.remove("username");
                            editor.apply();
                            Toast.makeText(getApplicationContext(), String.format("%s has been logged out", sharedPreferences.getString("username", null)), Toast.LENGTH_LONG).show();
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

        // The embedded method will be called when ScanStatusQR activity returns
        // back to the Scoreboard to open a fragment of the user profile.
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == 20) {
                            Intent content = result.getData();
                            if (content != null) {
                                String username = content.getStringExtra("username");

                                ScoreboardUserFragment userFragment = new ScoreboardUserFragment();
                                Bundle args = new Bundle();

                                for (User u: scoreboard.getUsers()) {
                                    if (u.getName().equals(username)) {
                                        showUserProfile(u);
                                    }
                                }
                            }
                        }
                    }
                }
        );
    }

    /**
     * Opens the DialogFragment to show user's profile
     * @param user
     * pass the user in this method (get user details)
     */
    private void showUserProfile(User user) {
        ScoreboardUserFragment userFragment = new ScoreboardUserFragment();

        // Grab the QRs of the user that have been seen by others
        ArrayList<QR> qrSeen = new ArrayList<>();
        for (QR qr: user.getQrs()) {
            if (scoreboard.qrSeen(qr)) {
                qrSeen.add(qr);
            }
        }

        Bundle args = new Bundle();
        args.putParcelable("User", user);
        args.putParcelableArrayList("QRSeen", qrSeen);

        userFragment.setArguments(args);
        userFragment.show(getSupportFragmentManager(), "Show User");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ScoreboardActivity.this, ScanQR.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     * To update the rankingAdapter when the underlying array is updated.
     * @param isFilter
     *      This parameter mentions if the update is after filtering or new data is being
     */
    @Override
    public void update(boolean isFilter) {
        rankingAdapter.sort();

        if (!isFilter) {
            updatePlayerMetric();
        }
    }

    /**
     * Updates the player's ranking at the top
     */
    private void updatePlayerMetric() {
        // If the data for the player was fetched update player rank
        User player = scoreboard.getPlayer();

        if (player != null) {
            ((TextView) findViewById(R.id.scoreboard_player_ranking))
                    .setText(String.valueOf(rankingAdapter.getPosition(player) + 1));

            int score = 0;
            switch (board) {
                case 0: score = player.getTotalScore(); break;
                case 1: score = player.getHighestScore(); break;
                case 2: score = player.getTotalQrs(); break;
            }
        }
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
