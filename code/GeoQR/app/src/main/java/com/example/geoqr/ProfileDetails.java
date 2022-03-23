package com.example.geoqr;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class ProfileDetails extends AppCompatActivity {

    TextView detail_content, detail_score, detail_date, detail_loc, detail_comment, detail_hex;
    ImageView detail_img;
    FirebaseFirestore db;
    Button detail_edit, detail_o, detail_x, detail_add_img, detail_del_img;
    EditText detail_edit_bar;
    String new_comment, username, longitude, latitude;
    String content, comment, date, score, image, hex;
    ActivityResultLauncher<Intent> activityResultLauncher;
    private final String TAG = "Sample";


    @Override
    protected void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        setContentView(R.layout.qr_detail);
        Objects.requireNonNull(getSupportActionBar()).hide();
        Intent intent = getIntent();
        hex = intent.getStringExtra("QR");
        String notice = "Click Edit Button To Add";
        String null_notice = "null";

        db = FirebaseFirestore.getInstance();
        detail_content = findViewById(R.id.detail_content);
        detail_comment = findViewById(R.id.detail_comment);
        detail_score = findViewById(R.id.detail_score);
        detail_date = findViewById(R.id.detail_date);
        detail_loc = findViewById(R.id.detail_loc);
        detail_img = findViewById(R.id.detail_img);
        detail_edit = findViewById(R.id.detail_edit);
        detail_o = findViewById(R.id.detail_o);
        detail_x = findViewById(R.id.detail_x);
        detail_edit_bar = findViewById(R.id.detail_edit_bar);
        detail_hex = findViewById(R.id.detail_hex);
        detail_add_img = findViewById(R.id.detail_add_image);
        detail_del_img = findViewById(R.id.detail_del_image);
        FloatingActionButton back_btn = findViewById(R.id.detail_back);

        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    Bitmap add_img = (Bitmap) bundle.get("data");
                    detail_img.setImageBitmap(add_img);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    add_img.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] bytes = stream.toByteArray();
                    Gson gson = new Gson();
                    String byte_array = gson.toJson(bytes);
                    updateImg(byte_array);
                    detail_img.setVisibility(View.VISIBLE);
                    detail_img.setImageBitmap(add_img);
                }
            }
        });

        db.collection("Users").document(username).collection("QR codes").document(hex).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        content = documentSnapshot.getString("Content");
                        comment = documentSnapshot.getString("Comment");
                        date = documentSnapshot.getString("Date");
                        score = documentSnapshot.getString("Score");
                        image = documentSnapshot.getString("Bytes Array");
                        detail_hex.setText(hex);
                        detail_content.setText(content);
                        detail_score.setText(score);
                        detail_date.setText(date);
                        if (!comment.equals("")) {
                            detail_comment.setText(comment);
                        }
                        if (!image.equals("null")) {
                            Gson gson1 = new Gson();
                            byte[] array = gson1.fromJson(image, byte[].class);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
                            detail_img.setImageBitmap(bitmap);
                            detail_img.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("failed access in user_ref ProfileDetail");
                    }
                });

        db.collection("QR codes").document(hex).collection("Users").document(username).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        longitude = documentSnapshot.getString("Longitude");
                        latitude = documentSnapshot.getString("Latitude");
                        if (longitude == null) {
                            detail_loc.setText(null_notice);
                        }
                        else if (!longitude.equals("null") && !latitude.equals("null")) {
                            String location = String.format("[%s, %s]", latitude, longitude);
                            detail_loc.setText(location);
                        }
                        else {
                            detail_loc.setText(null_notice);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("failed access in qr_ref ProfileDetail");
                    }
                });

        detail_add_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cam.resolveActivity(getPackageManager()) != null) {
                    activityResultLauncher.launch(cam);
                }
            }
        });

        detail_del_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Users").document(username).collection("QR codes").document(hex).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                image = documentSnapshot.getString("Bytes Array");
                                assert image != null;
                                if (image.equals("null")) {
                                    Toast.makeText(getApplicationContext(), "There is no image to remove", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    detail_img.setVisibility(View.GONE);
                                    deleteImg();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("failed access in user_ref ProfileDetail");
                            }
                        });
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(ProfileDetails.this, ProfilePage.class);
                back.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(back);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        detail_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detail_comment.setVisibility(View.GONE);
                detail_o.setVisibility(View.VISIBLE);
                detail_x.setVisibility(View.VISIBLE);
                detail_edit.setVisibility(View.GONE);
                detail_edit_bar.setVisibility(View.VISIBLE);
                getComment();
                detail_edit_bar.requestFocus();
                showKeyboard(ProfileDetails.this);
                detail_o.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hideKeyboard(ProfileDetails.this);
                        detail_edit.setVisibility(View.VISIBLE);
                        detail_o.setVisibility(View.GONE);
                        detail_x.setVisibility(View.GONE);
                        new_comment = detail_edit_bar.getText().toString();
                        detail_edit_bar.setVisibility(View.GONE);
                        detail_comment.setVisibility(View.VISIBLE);
                        detail_comment.setText(new_comment);
                        db.collection("Users").document(username).collection("QR codes").document(hex)
                                .update("Comment", new_comment)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "Comment Updated Successfully");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Comment Updated Unsuccessfully");
                                    }
                                });
                        if (new_comment.equals("")) {
                            detail_comment.setText(notice);
                        }
                    }
                });
                detail_x.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hideKeyboard(ProfileDetails.this);
                        detail_comment.setVisibility(View.VISIBLE);
                        detail_o.setVisibility(View.GONE);
                        detail_x.setVisibility(View.GONE);
                        detail_edit_bar.setVisibility(View.GONE);
                        detail_edit.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private void updateImg(String byte_array) {
        db.collection("Users").document(username).collection("QR codes").document(hex)
                .update("Bytes Array", byte_array)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Successfully add image in ProfileDetail");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Fail to add image in ProfileDetail");
                    }
                });
    }

    private void deleteImg() {
        db.collection("Users").document(username).collection("QR codes").document(hex)
                .update("Bytes Array", "null")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Successfully remove image in ProfileDetail");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Fail to remove image in ProfileDetail");
                    }
                });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void showKeyboard(Activity activity) {
        if (activity.getCurrentFocus().requestFocus()) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.showSoftInput(activity.getCurrentFocus(), InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void getComment() {
        db.collection("Users").document(username).collection("QR codes").document(hex).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        comment = documentSnapshot.getString("Comment");
                        assert comment != null;
                        if (comment.equals("")) {
                            detail_edit_bar.setText("");
                        }
                        else {
                            detail_edit_bar.setText(comment);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Fail to get comment in ProfileDetails");
                    }
                });
    }
}
