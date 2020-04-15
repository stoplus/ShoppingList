package ru.deliveon.lists.addEdit;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

import ru.deliveon.lists.R;
import ru.deliveon.lists.database.entity.Lists;
import ru.deliveon.lists.interfaces.AddEditListInterface;

public class AddEditListDialog extends DialogFragment {
    private AddEditListInterface datable;
    private View colorBtn;
    private int colorForStartDialog;

    @Override //The onAttach () method is called at the beginning of the fragment's life cycle
    public void onAttach(Context context) {
        super.onAttach(context);
        datable = (AddEditListInterface) context;
    } // onAttach

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Lists lists = null;
        int selectedColor = 0;
        String selectedName = "";

        if (getArguments() != null) {
            lists = getArguments().getParcelable("lists");
            selectedColor = getArguments().getInt("color", 0);
            selectedName = getArguments().getString("name", "");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        @SuppressLint("InflateParams") final View view = Objects.requireNonNull(getActivity()).
                getLayoutInflater().inflate(R.layout.new_list, null);
        EditText input = view.findViewById(R.id.editTextNewList);
        TextView addEditBtn = view.findViewById(R.id.btnAdd);
        colorBtn = view.findViewById(R.id.item_color);

        int image = R.drawable.add;
        String title = getResources().getString(R.string.create_new_list);
        addEditBtn.setText(getResources().getString(R.string.add));


        //редактируем
        if (lists != null) {
            input.setText(selectedName.isEmpty() ? lists.getListName() : selectedName);
            title = getResources().getString(R.string.edit_name_list);
            addEditBtn.setText(getResources().getString(R.string.edit));
            image = R.drawable.edit;
        }else {
            input.setText(selectedName);
        }

        //присваиваем цвет
        if (selectedColor != 0) {
            // присваиваем выбранный цвет
            colorForStartDialog = selectedColor;
        } else if (lists != null) {
            //берем из базы
            colorForStartDialog = lists.getColor();
        } else {
            //если не выбирали цвет, берем по умолчанию
            colorForStartDialog = ContextCompat.getColor(view.getContext(), R.color.colorList);//2
        }

        colorBtn.setBackgroundColor(colorForStartDialog);

        final Lists finalLists = lists;
        builder.setTitle(title)
                .setIcon(image)
                .setView(view);
        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialog1 -> {
            Button btnCancel = getDialog().findViewById(R.id.btnCancel);
            Button btnAdd = getDialog().findViewById(R.id.btnAdd);

            //выбор цвета айтема списка
            colorBtn.setOnClickListener(v -> {
                datable.openPicker(colorForStartDialog, finalLists, input.getText().toString());
                dialog1.dismiss();
            });

            btnAdd.setOnClickListener(v -> {
                String name = input.getText().toString();
                if (!name.isEmpty()) {
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                    if (finalLists == null) {
                        datable.addList(new Lists(name, colorForStartDialog));
                    } else {
                        finalLists.setListName(name);
                        finalLists.setColor(colorForStartDialog);
                        datable.update(finalLists);
                    }
                    dialog1.dismiss();
                } else
                    Snackbar.make(view, getResources().getString(R.string.enter_name_list), Snackbar.LENGTH_SHORT).show();
            });

            btnCancel.setOnClickListener(v -> dialog1.dismiss());
        });

        return dialog;
    } // onCreateDialog


}
