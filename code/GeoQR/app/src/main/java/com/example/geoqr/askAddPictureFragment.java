package com.example.geoqr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class askAddPictureFragment extends DialogFragment {

    private OnfragInteractionListener listener;
    private boolean return_val;

    public interface OnfragInteractionListener{
        void onOkPressed(boolean b);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnfragInteractionListener){
            listener = (OnfragInteractionListener) context;
        }else{
            throw new RuntimeException(context.toString()+"expction");
        }
    }

    public static askAddPictureFragment ask (boolean b) {
        Bundle bun = new Bundle();
        bun.putBoolean("bool",b);
        askAddPictureFragment fragment = new askAddPictureFragment();
        fragment.setArguments(bun);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.ask_add_pic, null);

        Bundle bundle = getArguments();
        if (bundle != null){
            return_val = bundle.getBoolean("bool");
        }

        AlertDialog.Builder bulider = new AlertDialog.Builder(getContext());

        return bulider
                .setView(view)
                .setTitle("Add new city")
                .setNegativeButton("Cancle", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return_val = false;
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return_val = true;
                    }
                }).create();
    }
}
