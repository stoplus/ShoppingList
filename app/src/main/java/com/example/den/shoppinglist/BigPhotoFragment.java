package com.example.den.shoppinglist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;

import java.util.Objects;

public class BigPhotoFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = Objects.requireNonNull(getActivity()).getLayoutInflater().inflate(R.layout.big_foto, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(view);
        return builder.create();
    }
}
