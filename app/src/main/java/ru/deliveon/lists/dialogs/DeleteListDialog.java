package ru.deliveon.lists.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Objects;

import ru.deliveon.lists.R;
import ru.deliveon.lists.database.entity.Lists;
import ru.deliveon.lists.interfaces.DeleteListInterface;

public class DeleteListDialog extends DialogFragment {
    private DeleteListInterface datable;

    @Override // Метод onAttach() вызывается в начале жизненного цикла фрагмента
    public void onAttach(Context context) {
        super.onAttach(context);
        datable = (DeleteListInterface) context;
    } // onAttach

    @NonNull // построить диалог с получением данных из активности и обработчиком кнопки
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Lists lists = (Lists) Objects.requireNonNull(getArguments()).getSerializable("lists");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle(getResources().getString(R.string.confirmation))
                .setIcon(R.drawable.warning)
                .setMessage(getResources().getString(R.string.delete_select_list))
                .setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> {
                    // отмена удаления
                    datable.cancelDeleteList();
                })
                .setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {
                    // удалить выбранный элемент списка при помощи метода интерфейса
                    datable.deleteList(lists);
                });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    } // onCreateDialog
}
