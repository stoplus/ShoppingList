package ru.deliveon.lists.addEdit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import ru.deliveon.lists.R;
import ru.deliveon.lists.interfaces.CameraOrGaleryInterface;

public class CameraOrGalery extends DialogFragment {

    private CameraOrGaleryInterface cameraOrGaleryInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        cameraOrGaleryInterface = (CameraOrGaleryInterface) context;
    } // onAttach

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(getResources().getString(R.string.camera_or_galery))
                .setTitle(getResources().getString(R.string.where_take_pictures))
                .setIcon(R.drawable.question)
                .setNegativeButton(getResources().getString(R.string.galery), (dialog, which) ->
                        cameraOrGaleryInterface.choiceForPhoto(true))
                .setPositiveButton(getResources().getString(R.string.camera), (dialog, which) ->
                        cameraOrGaleryInterface.choiceForPhoto(false));
        return builder.create();
    }
}
