package com.example.geoqr;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class ListFragment extends DialogFragment {
    private ImageView photo;
    private EditText comments;
    private OnFragmentInteractionListener listener;
    final String TAG = "Sample";
    FirebaseFirestore db;

    public interface OnFragmentInteractionListener {
        void onDeletePressed(ListEntry entry);
    }

    @Override public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    static ListFragment newInstance(ListEntry entry) {
        Bundle args = new Bundle();
        args.putSerializable("entry", entry);

        ListFragment fragment = new ListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_list_entry, null);
        comments = view.findViewById(R.id.comments);
        photo = view.findViewById(R.id.qr_image);
        Picasso.get()
                .load("https://firebasestorage.googleapis.com/v0/b/qrdatabase-301geoqr.appspot.com/o/testqr.png?alt=media&token=49e71a4f-e2b6-4792-a5b7-95b03535926b")
                .into(photo);


        ListEntry editEntry = (ListEntry) getArguments().getSerializable("entry");
        String qrcode = editEntry.getQrcode();
        db = FirebaseFirestore.getInstance();


        final DocumentReference documentReference = db.collection("Users").document("3FmLnxuiGMAJxStHRqMq").collection("QR codes").document(qrcode);


        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String comm = documentSnapshot.getString("Comment");
                comments.setText(comm);
            }
        });


        Bundle entryBundle = getArguments();
        ListEntry entry = (ListEntry) entryBundle.getSerializable("entry");

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        return builder
                .setView(view)
                .setNegativeButton("DELETE QR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onDeletePressed(entry);
                    }
                })
                .setPositiveButton("Close Menu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String comm = comments.getText().toString();


                        documentReference
                                .update("Comment", comm)
                                .addOnSuccessListener((OnSuccessListener) (aVoid) -> {
                                    Log.d(TAG, "Data added successfully");
                                })
                                .addOnFailureListener((e) -> {
                                    Log.d(TAG, "Data could not be added");
                                });

                    }
                }).create();


    }
}
