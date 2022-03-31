package com.example.geoqr;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * The Admin class talks to the Database and manages state for the AdminPage class,
 * Primarily, manages the qrAdapter & the playerAdapter for the corresponding ListViews.
 * Also manages 2 ArrayList called playerSelection & qrSelection to track, which
 * QRs or Players the Owner has selected to delete.
 */
public class Admin {
    // Adapters
    private AdminPlayerAdapter playerAdapter;
    private AdminQRAdapter qrAdapter;

    // DB
    FirebaseFirestore db;

    // Selection
    ArrayList<AdminPlayerTuple> playerSelection = new ArrayList<>();
    ArrayList<AdminQRTuple> qrSelection = new ArrayList<>();

    /**
     * It generates an Admin class for the AdminPage
     * @param ctx
     *      The Context is used to manage ArrayAdapters for Player & QR data
     */
    public Admin(Context ctx) {
        // Adapters
        playerAdapter = new AdminPlayerAdapter(ctx, new ArrayList<>());
        qrAdapter = new AdminQRAdapter(ctx, new ArrayList<>());
    }

    /**
     * It populates the Array Adapters by fetching data from the db
     */
    public void fetch() {
        // Get List of QR codes
        db = FirebaseFirestore.getInstance();
        Log.d("Debug", "Coming to fetch");

        // Get List of Players
        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        AdminPlayerTuple player = new AdminPlayerTuple(document.getId(),
                                Integer.parseInt((String) document.get("Total Score")));
                        playerAdapter.add(player);

                        // Add Players QR to qrAdapter
                        fetchQRsOfPlayer(player);
                    }
                    Log.d("Debug", "Finishing to fetch");
                }
                else {
                    Log.d("Error:", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void fetchQRsOfPlayer(AdminPlayerTuple player) {
        db.collection("Users").document(player.getName())
                .collection("QR codes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot qrData : task.getResult()) {
                        String content = (String) qrData.get("Content");
                        String score = (String) qrData.get("Score");

                        if (score == null || content == null) continue;

                        qrAdapter.add(new AdminQRTuple(qrData.getId(), content, player.getName(),
                                Integer.parseInt(score)));
                    }
                }
            }
        });
    }

    /**
     * This method deletes selected QRCodes from the database and updates the corresponding the ArrayAdapter
     * The selected QRCodes is stored in private qrSelection List in the class
     */
    public void deleteQRCodes() {
        for (AdminQRTuple qrTuple: qrSelection) {
            if (db != null) {
                // 1. Delete that specific user from the corresponding document in
                // "QR code" Collection
                db.collection("QR codes")
                        .document(qrTuple.getId())
                        .collection("Users").document(qrTuple.getPlayer())
                        .delete();

                // 2. Delete QR code from User Collection
                db.collection("Users")
                        .document(qrTuple.getPlayer())
                        .collection("QR codes").document(qrTuple.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    // 3. Update scores of the User
                                    String username = qrTuple.getPlayer();
                                    for (int i = 0; i < playerAdapter.getCount(); i++) {
                                        if (playerAdapter.getItem(i).getName().equals(username)) {
                                            updateUserScore(i);
                                            break;
                                        }
                                    }
                                }
                            });
            }

            qrAdapter.remove(qrTuple);
        }

        qrSelection.clear();
    }

    /**
     * Updates the score of the User in DB and updates the score locally
     * @param pos
     *      This is the position of the user in the playerAdapter
     */
    private void updateUserScore(int pos) {
        // Update the scores for the user
        db.collection("Users").document(playerAdapter.getItem(pos).getName())
                .collection("QR codes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int tscore = 0;
                int hscore = -1;
                int lscore = -1;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot qrData : task.getResult()) {
                        int qrScore = Integer.parseInt((String) qrData.get("Score"));
                        tscore += qrScore;
                        hscore = Math.max(hscore, qrScore);
                        lscore = lscore == -1 ? qrScore : Math.min(lscore, qrScore);
                    }
                }

                // 1. Update the score(s) in DB
                Map<String, Object> scores = new HashMap<>();
                scores.put("Highest Score", String.valueOf(hscore));
                scores.put("Lowest Score", String.valueOf(lscore));
                scores.put("Total Score", String.valueOf(tscore));

                db.collection("Users")
                        .document(playerAdapter.getItem(pos).getName())
                        .update(scores);

                // 2. Update the score in the table locally
                playerAdapter.getItem(pos).setScore(tscore);
                playerAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * This method deletes selected Players from the database and updates the corresponding ArrayAdapter
     * The selected players is stored in private playerSelection List in the class
     */
    public void deletePlayers() {
        for (AdminPlayerTuple playerTuple: playerSelection) {
            if (db != null) {
                // 1. Delete this user's entry in any document in the "QR codes" collection
                db.collection("Users")
                        .document(playerTuple.getName())
                        .collection("QR codes")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<AdminQRTuple> toRemove = new ArrayList<>();

                            for (QueryDocumentSnapshot qrData: task.getResult()) {
                                // Delete the User entry from the
                                // correspond "Users" collection under "QR codes"
                                db.collection("QR codes")
                                        .document(qrData.getId())
                                        .collection("Users")
                                        .document(playerTuple.getName())
                                        .delete();
                                // 2. Locally Update the qrAdapter for this corresponding user and qr
                                for (int i = 0; i < qrAdapter.getCount(); i++) {
                                    AdminQRTuple qrInAdapter = qrAdapter.getItem(i);

                                    // If the QR and the User equals to the removed item
                                    if (qrInAdapter.getPlayer().equals(playerTuple.getName()) && qrInAdapter.getId().equals(qrData.getId())) {
                                        toRemove.add(qrInAdapter);
                                    }
                                }

                                // Remove the QRs from the Table
                                for (AdminQRTuple qrTupleToRemove : toRemove) {
                                    qrAdapter.remove(qrTupleToRemove);
                                }

                                qrAdapter.notifyDataSetChanged();
                            }

                            // Once the User references are deleted:
                            // 3. Delete the user from "Users" collection
                            db.collection("Users").document(playerTuple.getName()).delete();
                        }
                        else {
                            // TODO: User can't be deleted
                        }
                    }
                });


            }

            playerAdapter.remove(playerTuple);
        }

        playerSelection.clear();
    }

    /**
     * The method adds the selected QR in the ListView to the qrSelection List
     * @param pos
     *      The pos corresponds to the position the ListView and the qrAdapter
     */
    public void addSelectedQRAt(int pos) {
        AdminQRTuple qr = qrAdapter.getItem(pos);
        if (!qrSelection.contains(qr)) {
            qrSelection.add(qr);
        }
    }

    /**
     * The method returns if the qrSelection List is empty or not
     * @return qrSelection.isEmpty()
     */
    public boolean noQRSelected() {
        return qrSelection.isEmpty();
    }

    /**
     * The method removes the selected QR in the ListView from the qrSelection List
     * @param pos
     *      The pos corresponds to the position the ListView and the qrAdapter
     */
    public void removeSelectedQRAt(int pos) {
        qrSelection.remove(qrAdapter.getItem(pos));
    }

    /**
     * The method checks if the selected QR in the ListView is the qrSelection List
     * @param pos
     *      The pos corresponds to the position the ListView and the qrAdapter
     * @return qrSelection.contains(qrAdapter.getItem(pos)
     */
    public boolean qrSelectedAt(int pos) {
        return qrSelection.contains(qrAdapter.getItem(pos));
    }

    /**
     * The method returns the position of the selected QR in the ListView or qrAdapter
     * @return
     *      Returns the position as an array of Integers
     */
    public ArrayList<Integer> getSelectedQRIndices() {
        ArrayList<Integer> indices = new ArrayList<>();

        for (AdminQRTuple q: qrSelection) {
            indices.add(qrAdapter.getPosition(q));
        }

        return indices;
    }

    /**
     * The method clears the qrSelection list
     */
    public void resetQRSelection() {
        qrSelection.clear();
    }

    /**
     * THe method clears the playerSelection list
     */
    public void resetPlayerSelection() {
        playerSelection.clear();
    }

    /**
     * The method returns the position of the selected players in the ListView or qrAdapter
     * @return
     *      Returns the position as an array of Integers
     */
    public ArrayList<Integer> getSelectedPlayerIndices() {
        ArrayList<Integer> indices = new ArrayList<>();

        for (AdminPlayerTuple p: playerSelection) {
            indices.add(playerAdapter.getPosition(p));
        }

        return indices;
    }

    /**
     * The method checks if the selected player in the ListView is the playerSelection List
     * @param pos
     *      The pos corresponds to the position the ListView and the playerAdapter
     * @return playerSelection.contains(playerAdapter.getItem(pos)
     */
    public boolean playerSelectedAt(int pos) {
        return playerSelection.contains(playerAdapter.getItem(pos));
    }

    /**
     * The method adds the selected player in the ListView to the playerSelection List
     * @param pos
     *      The pos corresponds to the position the ListView and the playerAdapter
     */
    public void addSelectedPlayerAt(int pos) {
        AdminPlayerTuple player = playerAdapter.getItem(pos);

        if (!playerSelection.contains(player)) {
            playerSelection.add(player);
        }
    }

    /**
     * The method removes the selected player in the ListView from the playerSelection List
     * @param pos
     *      The pos corresponds to the position the ListView and the playerAdapter
     */
    public void removeSelectedPlayerAt(int pos) {
        playerSelection.remove(playerAdapter.getItem(pos));
    }

    /**
     * The method checks if the playerSelection List is empty or not
     * @return playerSelection.isEmpty()
     */
    public boolean noPlayerSelected() {
        return playerSelection.isEmpty();
    }

    /**
     * The method returns the playerAdapter
     * @return playerAdapter
     */
    public AdminPlayerAdapter getPlayerAdapter() {
        return playerAdapter;
    }

    /**
     * The method returns the qrAdapter
     * @return qrAdapter
     */
    public AdminQRAdapter getQRAdapter() {
        return qrAdapter;
    }
}
