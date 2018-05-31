package com.example.den.shoppinglist.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.den.shoppinglist.R;
import com.example.den.shoppinglist.entity.Lists;
import com.example.den.shoppinglist.interfaces.AddEditListInterface;

import java.util.Objects;

public class AddEditListDialog extends DialogFragment {
    private AddEditListInterface datable;
    private EditText input;

    @Override //The onAttach () method is called at the beginning of the fragment's life cycle
    public void onAttach(Context context) {
        super.onAttach(context);
        datable = (AddEditListInterface) context;
    } // onAttach

    String title;
    String positiveButton;
    int image;

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Lists lists = null;
        if (getArguments() != null) {
            lists = getArguments().getParcelable("lists");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        @SuppressLint("InflateParams") final View view = Objects.requireNonNull(getActivity()).
                getLayoutInflater().inflate(R.layout.new_list, null);
        input = view.findViewById(R.id.editTextNewList);

        if (lists == null) {
            title = getResources().getString(R.string.create_new_list);
            positiveButton = getResources().getString(R.string.add);
            image = R.mipmap.add;
        } else {
            input.setText(lists.getListName());
            title = getResources().getString(R.string.edit_name_list);
            positiveButton = getResources().getString(R.string.edit);
            image = R.mipmap.edit;
        }

        final Lists finalLists = lists;
        builder.setTitle(title)
                .setIcon(image)



                .setView(view);
        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button btnCancel = getDialog().findViewById(R.id.btnCancel);
                Button btnAdd = getDialog().findViewById(R.id.btnAdd);

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = input.getText().toString();
                        if (!name.isEmpty()) {
                            if (finalLists == null) {
                                datable.addList(new Lists(name));
                            } else {
                                finalLists.setListName(name);
                                datable.update(finalLists);
                            }
                            dialog.dismiss();
                        }else
                        Snackbar.make(view, getResources().getString(R.string.enter_name_list),
                                Snackbar.LENGTH_SHORT).show();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        return dialog;
    } // onCreateDialog
}
