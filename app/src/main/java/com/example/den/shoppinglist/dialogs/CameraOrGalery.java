package com.example.den.shoppinglist.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.example.den.shoppinglist.R;
import com.example.den.shoppinglist.interfaces.CameraOrGaleryInterface;

public class CameraOrGalery extends DialogFragment {

    private CameraOrGaleryInterface cameraOrGaleryInterface;

    @Override // Метод onAttach() вызывается в начале жизненного цикла фрагмента
    public void onAttach(Context context) {
        super.onAttach(context);
        cameraOrGaleryInterface = (CameraOrGaleryInterface) context;
    } // onAttach

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Выбрать фото из галереи или снять на камеру?")
                .setTitle("Откуда возмем фото?")
                .setIcon(R.mipmap.question)
                .setNegativeButton("Галерея", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cameraOrGaleryInterface.choiceForPhoto(true);
                    }
                }).setPositiveButton("Камера", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cameraOrGaleryInterface.choiceForPhoto(false);
            }
        });
        return builder.create();
    }
}
