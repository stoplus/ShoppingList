package com.example.den.shoppinglist.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.den.shoppinglist.R;
import com.example.den.shoppinglist.entity.Product;
import com.example.den.shoppinglist.interfaces.AddEditProductInterface;

public class AddEditListProductsDialog extends DialogFragment {
    private AddEditProductInterface datable;
    private String title;
    private String positiveButton;
    private int imageIcon;
    private SurfaceView surfaceView;
    private EditText editText;
    private ImageView imageView;

    @Override // Метод onAttach() вызывается в начале жизненного цикла фрагмента
    public void onAttach(Context context) {
        super.onAttach(context);
        datable = (AddEditProductInterface) context;
    } // onAttach



    @NonNull // построить диалог с получением данных из активности и обработчиком кнопки
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Product lists = null;
        if (getArguments() != null) {
            lists = getArguments().getParcelable("product");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //создаем вид
        View view = getActivity().getLayoutInflater().inflate(R.layout.add_edit_product, null);
        surfaceView = view.findViewById(R.id.surfaceView);
        editText = view.findViewById(R.id.editText);
        imageView = view.findViewById(R.id.imageView);
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        if (lists == null) {
            editText.setHint("Название");
            title = "Создание нового товара";
            positiveButton = "Добавить";
            imageIcon = R.mipmap.add;
        } else {
            editText.setText(lists.getNameProduct());
            title = "Изменение названия товара";
            positiveButton = "Изменить";
            imageIcon = R.mipmap.edit;
        }

        final Product finalLists = lists;
        builder.setTitle(title)
                .setIcon(imageIcon)
                .setView(view)
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNeutralButton("Добавить фото",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editText.getText().toString();
                        String link = null;
                        if (!name.isEmpty()) {
                            if (finalLists == null) {
                                //создаем новый
                                datable.addProduct(new Product(name, link, false));
                            } else {
                                //обновляем
                                finalLists.setNameProduct(name);
                                datable.updateProduct(finalLists);
                            }
                        }
                    }
                });
        return builder.create();
    } // onCreateDialog
}