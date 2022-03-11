package com.example.geoqr;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

public class Test_passtoDB {
    FirebaseFirestore db;

    private addQR test(){
        addQR a = new addQR();
        return a;
    }

    @Test
    void tests(){
        db = FirebaseFirestore.getInstance();
        final CollectionReference user_Ref = db.collection("Users");
        final CollectionReference QR_ref = db.collection("QR codes");
        // same check if document exist, have not solve this yet
    }
}
