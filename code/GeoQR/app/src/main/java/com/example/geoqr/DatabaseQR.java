package com.example.geoqr;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DatabaseQR extends AppCompatActivity {

    FirebaseFirestore db;
    CollectionReference user_ref;
    CollectionReference QR_ref;
    private String UserID, QRCodeID, Geo;
    private String userID, userName, QRScore;
    ArrayList<Integer> tempIntArray = new ArrayList<>();
    ArrayList<String> tempStringArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        user_ref = db.collection("Users");
        QR_ref = db.collection("QR codes");
    }

    public void setUsername(String UserID) {
        this.UserID = UserID;
    }

    public String getUserName() {
        return "Test";
    }

    public void setQR(String QRCodeID) {
        this.QRCodeID = QRCodeID;
    }


    // going to test if addOnComplete or addOnSuccess is more suitable
//    public String getUserID() {
//        DocumentReference getID = user_ref.document(UserID);
//
//        getID.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                userID = documentSnapshot.getString("ID");
//            }
//        });


//        getID.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                        userID = document.getString("ID");
//                    }
//                    else {
//                        Log.d(TAG, "No such document");
//                    }
//                }
//                else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });

//        return userID;
//    }

//    public String getUserName() {
//        DocumentReference getName = user_ref.document(UserID);
//        getName.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                userName = documentSnapshot.getString("username");
//            }
//        });
//        return userName;
//    }

    // to be written
    public ArrayList<String> getContactList() {
        return tempStringArray;
    }

    public String getQRScore() {
        DocumentReference getQR = QR_ref.document(QRCodeID);
        getQR.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                QRScore = documentSnapshot.getString("Score");
            }
        });
        return QRScore;
    }

    // to be written
    public ArrayList<String> getUserList() {
        return tempStringArray;
    }

}
