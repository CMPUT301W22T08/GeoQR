package com.example.geoqr;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DatabaseQR{

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference user_ref = db.collection("Users");
    final CollectionReference QR_ref = db.collection("QR codes");
    private String QRTotalScore, QRHighestScore, QRLowestScore;
    String contact;
    private String username;

    public DatabaseQR(String userName){
        username = userName;
    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
//        username = sharedPreferences.getString("username", null);
//
//    }

    public String getContact() {
        DocumentReference d = user_ref.document(username);
                d.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        contact = documentSnapshot.getString("Contact");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

        return contact;
    }

    public String getQRTotalScore() {
        user_ref.document(username).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        QRTotalScore = documentSnapshot.getString("Total Score");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
        return QRTotalScore;
    }

    public String getQRHighestScore() {
        final CollectionReference user_ref = db.collection("Users");
        user_ref.document(username).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        QRHighestScore = documentSnapshot.getString("Highest Score");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
        return QRHighestScore;
    }

    public String getQRLowestScore() {
        user_ref.document(username).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        QRLowestScore = documentSnapshot.getString("Lowest Score");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
        return QRLowestScore;
    }


}
