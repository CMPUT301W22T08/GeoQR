package com.example.geoqr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

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

    private ScoreboardRankingAdapter rankingAdapter;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        Objects.requireNonNull(getSupportActionBar()).hide();

        rankingView = findViewById(R.id.scoreboard_ranking);
        tabs = findViewById(R.id.scoreboard_tabs);
        searchBar = findViewById(R.id.scoreboard_searchbar);
        scanBtn = findViewById(R.id.scoreboard_scan_status_qr);
        backBtn = findViewById(R.id.scoreboard_go_back);

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

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Goto the ScanStatusQR
                Intent scan = new Intent(ScoreboardActivity.this, ScanStatusQR.class);
                activityResultLauncher.launch(scan);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
            }
        });

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

        scoreboard = new Scoreboard(this, sharedPreferences.getString("username", null));
        rankingAdapter = new ScoreboardRankingAdapter((Context) this, scoreboard.getUsers());

        rankingAdapter.setBoard(board);
        rankingView.setAdapter(rankingAdapter);

        rankingView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showUserProfile(rankingAdapter.getItem(i));
            }
        });

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

    @Override
    public void update(boolean isFilter) {
        rankingAdapter.sort();

        if (!isFilter) {
            updatePlayerMetric();
        }
    }

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
}
