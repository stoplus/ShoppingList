package com.example.den.shoppinglist.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.den.shoppinglist.R;
import com.example.den.shoppinglist.entity.Lists;
import com.example.den.shoppinglist.interfaces.AddEditListInterface;
import com.example.den.shoppinglist.interfaces.DeleteListInterface;

public class AddEditListDialog extends DialogFragment {
    private AddEditListInterface datable;

    @Override // Метод onAttach() вызывается в начале жизненного цикла фрагмента
    public void onAttach(Context context) {
        super.onAttach(context);
        datable = (AddEditListInterface) context;
    } // onAttach

    String title;
    String positiveButton;
    int image;

    @NonNull // построить диалог с получением данных из активности и обработчиком кнопки
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Lists lists = null;
        if (getArguments() != null) {
            lists = getArguments().getParcelable("lists");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(getContext());
        input.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        if (lists == null) {
            input.setHint("Название");
            title = "Создание нового списка";
            positiveButton = "Добавить";
            image = R.mipmap.add;
        } else {
            input.setText(lists.getListName());
            title = "Изменение названия списка";
            positiveButton = "Изменить";
            image = R.mipmap.edit;
        }

        final Lists finalLists = lists;
        builder.setTitle(title)
                .setIcon(image)
                .setView(input)
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { dialog.cancel();
                    }
                })
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = input.getText().toString();
                        if (!name.isEmpty()) {
                            if (finalLists == null) {
                                datable.addList(new Lists(name));
                            } else {
                                finalLists.setListName(name);
                                datable.update(finalLists);
                            }
                        }
                    }
                });
        return builder.create();
    } // onCreateDialog
}
