package com.example.geoqr;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


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
    }

    /**
     * This method deletes selected QRCodes from the database and updates the corresponding the ArrayAdapter
     * The selected QRCodes is stored in private qrSelection List in the class
     */
    public void deleteQRCodes() {
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

    /**
     * This method deletes selected Players from the database and updates the corresponding ArrayAdapter
     * The selected players is stored in private playerSelection List in the class
     */
    public void deletePlayers() {
        for (AdminPlayerTuple playerTuple: playerSelection) {
            db.collection("Users").document(playerTuple.getName()).delete();
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
        qrSelection.add(qrAdapter.getItem(pos));
    }

    /**
     * The method returns if the qrSelection List is empty or not
     * @return
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
     * @return
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
     * @return
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
        playerSelection.add(playerAdapter.getItem(pos));
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
     * @return
     */
    public boolean noPlayerSelected() {
        return playerSelection.isEmpty();
    }

    /**
     * The method returns the playerAdapter
     * @return
     */
    public AdminPlayerAdapter getPlayerAdapter() {
        return playerAdapter;
    }

    /**
     * The method returns the qrAdapter
     * @return
     */
    public AdminQRAdapter getQRAdapter() {
        return qrAdapter;
    }
}
