package com.example.den.shoppinglist.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.example.den.shoppinglist.R;
import com.example.den.shoppinglist.entity.Lists;
import com.example.den.shoppinglist.interfaces.AddEditListInterface;

public class AddEditListDialog extends DialogFragment {
    private AddEditListInterface datable;

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
        final EditText input = new EditText(getContext());
        input.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        if (lists == null) {
            input.setHint(getResources().getString(R.string.name));
            title = getResources().getString(R.string.create_new_list);
            positiveButton = getResources().getString(R.string.add);
            image = R.mipmap.add;
        } else {
            input.setText(lists.getListName());
            title =  getResources().getString(R.string.edit_name_list);
            positiveButton = getResources().getString(R.string.edit);
            image = R.mipmap.edit;
        }

        final Lists finalLists = lists;
        builder.setTitle(title)
                .setIcon(image)
                .setView(input)
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
                        // TODO: 24.05.2018
//                        Snackbar.make(view,getResources().getString(R.string.enter_name_list), Snackbar.LENGTH_SHORT).show();
                    }
                });
        return builder.create();
    } // onCreateDialog
}
