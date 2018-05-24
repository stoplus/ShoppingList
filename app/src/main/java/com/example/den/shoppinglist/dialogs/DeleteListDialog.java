package com.example.den.shoppinglist.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.example.den.shoppinglist.R;
import com.example.den.shoppinglist.entity.Lists;
import com.example.den.shoppinglist.interfaces.DeleteListInterface;

import java.util.Objects;

public class DeleteListDialog extends DialogFragment {
    private DeleteListInterface datable;

    @Override // Метод onAttach() вызывается в начале жизненного цикла фрагмента
    public void onAttach(Context context){
        super.onAttach(context);
        datable = (DeleteListInterface) context;
    } // onAttach

    @NonNull // построить диалог с получением данных из активности и обработчиком кнопки
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Lists lists = Objects.requireNonNull(getArguments()).getParcelable("lists");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle(getResources().getString(R.string.confirmation))
                .setIcon(R.mipmap.warning)
                .setMessage(getResources().getString(R.string.delete_select_list))
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // удалить выбранный элемент списка при помощи метода интерфейса
                        datable.deleteList(lists);
                    }
                });
        return builder.create();
    } // onCreateDialog
}
