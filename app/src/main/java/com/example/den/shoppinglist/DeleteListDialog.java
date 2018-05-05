package com.example.den.shoppinglist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.example.den.shoppinglist.entity.Lists;
import com.example.den.shoppinglist.interfaces.DeleteListInterface;

public class DeleteListDialog extends DialogFragment {
    private DeleteListInterface datable;

    @Override // Метод onAttach() вызывается в начале жизненного цикла фрагмента
    public void onAttach(Context context){
        super.onAttach(context);
        datable = (DeleteListInterface) context;
    } // onAttach

    @NonNull // построить диалог с получением данных из активности и обработчиком кнопки
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Lists lists = getArguments().getParcelable("lists");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle("Подтверждение")
                .setIcon(R.mipmap.warning)
                .setMessage("Удалить выбранный список?")
                .setNegativeButton("Нет", null)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // удалить выбранный элемент списка при помощи метода интерфейса
                        datable.deleteList(lists);
                    }
                });
        return builder.create();
    } // onCreateDialog
}
