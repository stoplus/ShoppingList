package com.example.den.shoppinglist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.den.shoppinglist.entity.Lists;
import com.example.den.shoppinglist.interfaces.AddEditListInterface;
import com.example.den.shoppinglist.interfaces.DeleteListInterface;

public class AddEditListDialog extends DialogFragment {
    private AddEditListInterface datable;

    @Override // Метод onAttach() вызывается в начале жизненного цикла фрагмента
    public void onAttach(Context context){
        super.onAttach(context);
        datable = (AddEditListInterface) context;
    } // onAttach


    String title;
    String positiveButton;
    @NonNull // построить диалог с получением данных из активности и обработчиком кнопки
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Lists lists = getArguments().getParcelable("lists");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(getContext());

        if (lists == null) {
            input.setHint("Название");
            title = "Создание нового списка";
            positiveButton = "Добавить";
        } else {
            input.setText(lists.getListName());
            title = "Изменение названия списка";
            positiveButton = "Изменить";
        }

        builder.setTitle(title)
//                .setIcon(R.drawable.warning)
                .setMessage("Удалить выбранный список?")
                .setView(input)
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = input.getText().toString();
                        if (!name.isEmpty()) {
                            if (lists == null) {
                                datable.addList(new Lists(name));
                            } else {
                                lists.setListName(name);
                                datable.update(lists);
                            }
                        }
                    }
                });
        return builder.create();
    } // onCreateDialog

    

//    final EditText input = new EditText(getContext());
//    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.MATCH_PARENT,
//            LinearLayout.LayoutParams.MATCH_PARENT);
//        input.setLayoutParams(lp);
//        if (lists == null) {
//        input.setHint("Название");
//        title = "Создание нового списка";
//        positiveButton = "Добавить";
//    } else {
//        input.setText(lists.getListName());
//        title = "Изменение названия списка";
//        positiveButton = "Изменить";
//    }
//        alertDialog.setTitle(title)
//            .setView(input)
//                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//            String name = input.getText().toString();
//            if (!name.isEmpty()) {
//                if (lists == null) {
//                    addList(new Lists(name));
//                } else {
//                    lists.setListName(name);
//                    update(lists);
//                }
//            }
//        }
//    })
//            .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//            dialog.cancel();
//        }
//    });
//        alertDialog.show();
}
