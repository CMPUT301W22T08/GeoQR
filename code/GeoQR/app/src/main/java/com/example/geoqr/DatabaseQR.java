package com.example.geoqr;

import static androidx.constraintlayout.core.motion.MotionPaths.TAG;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;

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

    public void setUserID(String UserID) {
        this.UserID = UserID;
    }
    // 这后面应该还需要call什么然后db才能更新

    public void setQR(String QRCodeID) {
        this.QRCodeID = QRCodeID;
    }

    public void setGeo(String Geo) {
        this.Geo = Geo;
    }

    public String getGeo() {
        return this.Geo;
    }

    // going to test if addOnComplete or addOnSuccess is more suitable
    public String getUserID() {
        DocumentReference getID = user_ref.document(UserID);

//        getID.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                userID = documentSnapshot.getString("ID");
//            }
//        });

        // 大概。。看着有点怪，因为userid是field data，好像不是这样access的
        getID.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        userID = document.getString("ID");
                    }
                    else {
                        Log.d(TAG, "No such document");
                    }
                }
                else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        return userID;
    }

    public String getUserName() {
        // 大概应该是没什么问题，但是一个 .getId() 在document后面应该就可以解决了。。。
        DocumentReference getName = user_ref.document(UserID);
        getName.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userName = documentSnapshot.getString("username");
            }
        });
        return userName;
    }

    // to be written
    public ArrayList<String> getContactList() {
        return tempStringArray;
    }
    // 这后面应该还需要call什么然后db才能更新

    public String getQRScore() {
        // 目前感觉没什么问题
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
    public ArrayList<Integer> getGeoList() {
        return tempIntArray;
    }
    // 这后面应该还需要call什么然后db才能更新

    // to be written
    public ArrayList<String> getUserList() {
        return tempStringArray;
    }
    // 这后面应该还需要call什么然后db才能更新





}
