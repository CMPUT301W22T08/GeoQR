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

public class Admin {
    // Adapters
    private AdminPlayerAdapter playerAdapter;
    private AdminQRAdapter qrAdapter;

    // DB
    FirebaseFirestore db;

    // Selection
    ArrayList<AdminPlayerTuple> playerSelection = new ArrayList<>();
    ArrayList<AdminQRTuple> qrSelection = new ArrayList<>();

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

    public void deletePlayers() {
        for (AdminPlayerTuple playerTuple: playerSelection) {
            db.collection("Users").document(playerTuple.getName()).delete();
            playerAdapter.remove(playerTuple);
        }

        playerSelection.clear();
    }

    public void addSelectedQRAt(int pos) {
        qrSelection.add(qrAdapter.getItem(pos));
    }

    public boolean noQRSelected() {
        return qrSelection.isEmpty();
    }

    public void removeSelectedQRAt(int pos) {
        qrSelection.remove(qrAdapter.getItem(pos));
    }

    public boolean qrSelectedAt(int pos) {
        return qrSelection.contains(qrAdapter.getItem(pos));
    }

    public ArrayList<Integer> getSelectedQRIndices() {
        ArrayList<Integer> indices = new ArrayList<>();

        for (AdminQRTuple q: qrSelection) {
            indices.add(qrAdapter.getPosition(q));
        }

        return indices;
    }

    public void resetQRSelection() {
        qrSelection.clear();
    }

    public void resetPlayerSelection() {
        playerSelection.clear();
    }

    public ArrayList<Integer> getSelectedPlayerIndices() {
        ArrayList<Integer> indices = new ArrayList<>();

        for (AdminPlayerTuple p: playerSelection) {
            indices.add(playerAdapter.getPosition(p));
        }

        return indices;
    }

    public boolean playerSelectedAt(int pos) {
        return playerSelection.contains(playerAdapter.getItem(pos));
    }

    public void addSelectedPlayerAt(int pos) {
        playerSelection.add(playerAdapter.getItem(pos));
    }

    public void removeSelectedPlayerAt(int pos) {
        playerSelection.remove(playerAdapter.getItem(pos));
    }

    public boolean noPlayerSelected() {
        return playerSelection.isEmpty();
    }

    public AdminPlayerAdapter getPlayerAdapter() {
        return playerAdapter;
    }

    public AdminQRAdapter getQRAdapter() {
        return qrAdapter;
    }
}
