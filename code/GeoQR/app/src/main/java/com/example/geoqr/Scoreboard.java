package com.example.geoqr;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class Scoreboard {

    public interface RankingUpdatable {
        public void update(boolean isFilter);
    }

    ArrayList<User> users, original;

    HashMap<String, ArrayList<User>> commonQRs;
    FirebaseFirestore db;

    String playerName; // Player name
    User player; // The player object

    RankingUpdatable callee;

    public Scoreboard(RankingUpdatable callee, String playerName) {
        this.playerName = playerName;
        this.callee = callee;

        users = new ArrayList<>();
        original = new ArrayList<>();
        commonQRs = new HashMap<>();
        fetchUserData();
    }

    public void fetchUserData() {
        db = FirebaseFirestore.getInstance();

        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String username = document.getId();
                        int totalScore = Integer.parseInt((String) document.get("Total Score"));
                        int highestScore = Integer.parseInt((String) document.get("Highest Score"));

                        // Update the user's number of QR codes

                        fetchTotalQRs(username, totalScore, highestScore);
                    }
                    Log.d("Debug", "Finishing to fetch");
                }
                else {
                    Log.d("Error:", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void fetchTotalQRs(String username, int totalScore, int highestScore) {
        db.collection("Users").document(username)
                .collection("QR codes").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<QR> qrs = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                int score = Integer.parseInt((String) document.get("Score"));
                                QR qr = new QR((String) document.getId(),
                                               (String) document.get("Content"),
                                                username, score);
                                qrs.add(qr);
                            }

                            User user = new User(username, totalScore, highestScore, qrs);

                            // Update Common QRs
                            for (QR qr: qrs) {
                                if (!commonQRs.containsKey(qr.getContent())) {
                                    commonQRs.put(qr.getContent(), new ArrayList<>());
                                }

                                commonQRs.get(qr.getContent()).add(user);
                            }

                            if (user.getName().equals(playerName)) {
                                player = user;
                            }

                            original.add(user);
                            users.add(user);
                            callee.update(false); // Update the dependent class
                        }

                        else {
                            Log.i("G: ", "Can't fetch user QRs");
                        }
                    }
                });
    }

    public ArrayList<User> getUsers() {
        return users;
    }
    public User getUser(String name) {
        for (User user: users) {
            if (user.getName().equals(name)) {
                return user;
            }
        }

        return null;
    }

    public String getPlayerName() {
        return playerName;
    }

    public User getPlayer() {
        return player;
    }

    public void filterUsers(String query) {

        users.clear();

        if (query == null || query.length() == 0) {
            // set the Original result to return
            for (User user: original) {
                users.add(user);
            }
        }
        else {
            // Add the users whose usernames starts with the query string
            for (User user: original) {
                if (user.getName().startsWith(query)) {
                    users.add(user);
                }
            }
        }
        callee.update(true); // Notify change
    }
}
