package com.example.geoqr;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class DatabaseQR extends AppCompatActivity {

    FirebaseFirestore db;

    // getUserName
    // setUserName
    // getQRData
    // setQRData
    // getQRScore
    // setQRScore


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
    }

    public String getUserName() {
        return "Name";
    }





}
