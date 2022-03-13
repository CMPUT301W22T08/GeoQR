package com.example.geoqr;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;

import java.util.HashMap;

public class ProfilePageTest {

    FirebaseFirestore db;
    final String TAG = "Sample";


    public void addItem() {
        HashMap<String, String> data = new HashMap<>();
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Users");

        data.put("QR codes",  "rsz8IB2g4cIGe73YKRig");

        collectionReference
                .document()
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Data has been added successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data could not be added!" + e.toString());
                    }
                });

    }

    @Test
    public void deleteTest() {
        return;
    }
}
