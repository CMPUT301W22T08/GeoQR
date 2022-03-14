package com.example.geoqr;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.geoqr.RandomString;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginPage extends AppCompatActivity {

    private Button btnLogin, btnGenerate;
    private EditText etUsername;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        etUsername = findViewById(R.id.et_Username);
        btnGenerate = findViewById(R.id.btn_Generate);
        btnLogin = findViewById(R.id.btn_Login);



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String username = etUsername.getText().toString();
                String id = "";
                String contact = "";
                if (!TextUtils.isEmpty(username)) {  // if the username is not empty
                    Map<String, Object> user = new HashMap<>();
                    user.put("Username",username);
                    user.put("ID", id);
                    user.put("Contact", contact);

                    db.collection("Users")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "Document added:" + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG,"Error adding Document", e);
                                }
                            });

                } else {
                    Toast.makeText(LoginPage.this, "Username cannot be empty, please re-enter!", Toast.LENGTH_SHORT).show();

                }
            }
        });

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generate();

            }
        });
    }


    private void generate(){
        RandomString randomString = new RandomString();
        String result = randomString.generateAlphaNumeric(12);
        etUsername.setText(result);
    }

}