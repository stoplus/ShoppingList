package com.example.den.shoppinglist.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
    private Button btnPositive;
    private Button btnNegative;
    private Button btnAddPhoto;
    private Button btnCreatePhoto;
    private LinearLayout layoutBtn;
    private Dialog dialog;
    private final int CAMERA_CAPTURE = 1;
    private final int PIC_CROP = 2;
    private Uri picUri;
    private View view;
    private FragmentActivity current;

    @Override // Метод onAttach() вызывается в начале жизненного цикла фрагмента
    public void onAttach(Context context) {
        super.onAttach(context);
        datable = (AddEditProductInterface) context;
    } // onAttach

    @NonNull // создание диалога
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        Product lists = null;
        if (getArguments() != null) {
            lists = getArguments().getParcelable("product");
        }

        current = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(current);
        //создаем вид
        view = current.getLayoutInflater().inflate(R.layout.add_edit_product, null);
        surfaceView = view.findViewById(R.id.surfaceView);
        editText = view.findViewById(R.id.editText);
        imageView = view.findViewById(R.id.imageView);
        btnAddPhoto = view.findViewById(R.id.idAddPhoto);
        btnNegative = view.findViewById(R.id.idCancel);
        btnPositive = view.findViewById(R.id.idAdd);
        btnCreatePhoto = view.findViewById(R.id.idBtnCreatePhoto);
        layoutBtn = view.findViewById(R.id.idLayoutBtn);

        if (lists == null) {
            editText.setHint("Название");
            title = "Создание нового товара";
            btnPositive.setText(getResources().getString(R.string.add));
            imageIcon = R.mipmap.add;
        } else {
            editText.setText(lists.getNameProduct());
            title = "Изменение названия товара";
            btnPositive.setText(getResources().getString(R.string.edit));
            imageIcon = R.mipmap.edit;
        }

        final Product finalLists = lists;

        builder.setTitle(title)
                .setIcon(imageIcon)
                .setView(view);
        dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                btnPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                            dialog.dismiss();
                        }
                        Snackbar.make(view, "Введите название покупки", Snackbar.LENGTH_SHORT).show();
                    }
                });

                btnNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });

                btnAddPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layoutBtn.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                        editText.setVisibility(View.VISIBLE);
                        surfaceView.setVisibility(View.GONE);
                        btnCreatePhoto.setVisibility(View.GONE);

                        try {// Намерение для запуска камеры
                            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(captureIntent, CAMERA_CAPTURE);
                        } catch (ActivityNotFoundException e) {
                            // Выводим сообщение об ошибке
                            Snackbar.make(view, "Ваше устройство не поддерживает съемку", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        return dialog;
    } // onCreateDialog

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_CAPTURE) {
            if (resultCode == getActivity().RESULT_OK) {
                Log.d("ll", "kk");
                // successfully captured the image
                // display it in image view
//                previewCapturedImage();
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getActivity().getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getActivity().getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}