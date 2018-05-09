package com.example.den.shoppinglist;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.den.shoppinglist.dialogs.AddEditListProductsDialog;
import com.example.den.shoppinglist.dialogs.CameraOrGalery;
import com.example.den.shoppinglist.entity.Product;
import com.example.den.shoppinglist.interfaces.CameraOrGaleryInterface;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CameraImage extends AppCompatActivity implements CameraOrGaleryInterface {
    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.idAddPhoto)
    Button btnAddPhoto;
    @BindView(R.id.idCancel)
    Button btnCancel;
    @BindView(R.id.idAdd)
    Button btnAdd;
    @BindView(R.id.idLayoutBtn)
    LinearLayout layoutBtn;
    private final int CAMERA_CAPTURE = 1;
    private View view;
    private String finalPath = "";  //путь к файлу
    private Uri uri;
    private int idList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_image);
        ButterKnife.bind(this);

        Product lists = null;
        lists = getIntent().getParcelableExtra("product");
        idList = getIntent().getIntExtra("idList", -1);
        if (savedInstanceState != null) {
            finalPath = savedInstanceState.getString("finalPath");
            idList = savedInstanceState.getInt("idList");
            if (!finalPath.isEmpty()) {
                uri = Uri.fromFile(new File(finalPath));
                Picasso.with(this).load(uri).into(imageView);
            }
        }

        if (lists == null) {
            editText.setHint("Название");
//            title = "Создание нового товара";
            btnAdd.setText(getResources().getString(R.string.add));
        } else {
            editText.setText(lists.getNameProduct());
//            title = "Изменение названия товара";
            btnAdd.setText(getResources().getString(R.string.edit));
        }

        final Product finalProduct = lists;

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText.getText().toString();
                String link = null;
                if (!name.isEmpty()) {
                    if (finalProduct == null) {
                        //создаем новый
                        Intent intent = new Intent();
                        Product product = new Product(name, link, false);
                        intent.putExtra("product", product);
                        setResult(RESULT_OK, intent);//возращаем результат
                        finish();
//                        datable.addProduct(new Product(name, link, false));
                    } else {
                        //обновляем
                        finalProduct.setNameProduct(name);
                        Intent intent = new Intent();
                        intent.putExtra("productUpdate", finalProduct);
                        setResult(RESULT_OK, intent);//возращаем результат
                        finish();
//                        datable.updateProduct(finalLists);
                    }
                }else {
                    //Snackbar.make(view, "Введите название покупки", Snackbar.LENGTH_SHORT).show();
                    Toast.makeText(CameraImage.this, "Введите название покупки", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraImage.this, Products.class);
                intent.putExtra("idList", idList);
                setResult(RESULT_CANCELED, intent);//возращаем результат
                finish();
            }
        });

        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {// Намерение для запуска камеры
                    CameraOrGalery cameraOrGalery = new CameraOrGalery();
                    cameraOrGalery.show(getSupportFragmentManager(), "cameraOrGalery");
                } catch (ActivityNotFoundException e) {
                    // Выводим сообщение об ошибке
                    Snackbar.make(view, "Ваше устройство не поддерживает съемку", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_CAPTURE://reqCode авторизации в ВК
                if (resultCode == Activity.RESULT_OK) {
                    createPathPhoto();
                } else Toast.makeText(this, "Вы не сделали фото", Toast.LENGTH_LONG).show();
                break;
            case 111://reqCode системы при выборе картинок
                if (resultCode == Activity.RESULT_OK) {
                    Uri imageUri = data.getData();
                    Picasso.with(this).load(imageUri).into(imageView);
                } else Toast.makeText(this, "Вы не выбрали фото", Toast.LENGTH_LONG).show();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createPathPhoto() {
        //Создать новый курсор для получения файла Путь для большого изображения
        String[] largeFileProjection = {
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.DATE_ADDED
        };
        String largeFileSort = MediaStore.Images.ImageColumns._ID + " DESC";
        int dateEXTERNAL = 0;

        Cursor myCursorLargeE = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                largeFileProjection,
                null,
                null,
                largeFileSort);
        String largeImagePathE = "";

        try {
            if (myCursorLargeE != null && myCursorLargeE.getCount() > 0) {
                myCursorLargeE.moveToFirst();
                dateEXTERNAL = Integer.parseInt(myCursorLargeE.getString(myCursorLargeE.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED)));
                // путь к файлу.
                largeImagePathE = myCursorLargeE.getString(myCursorLargeE.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
            }
        } finally {
            myCursorLargeE.close();
        }
        //--------------------------
        Cursor myCursorLargeI = getContentResolver().query(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                largeFileProjection,
                null,
                null,
                largeFileSort);
        int dateINTERNAL = 0;
        String largeImagePathI = "";
        try {
            if (myCursorLargeI != null && myCursorLargeI.getCount() > 0) {
                myCursorLargeI.moveToFirst();
                dateINTERNAL = Integer.parseInt(myCursorLargeI.getString(myCursorLargeI.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED)));
                // путь к файлу
                largeImagePathI = myCursorLargeI.getString(myCursorLargeI.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
            }
        } finally {
            myCursorLargeI.close();
        }
        if (dateEXTERNAL > dateINTERNAL) {
            finalPath = largeImagePathE;
            Log.d("ggg", " dateEXTERNAL = " + dateEXTERNAL);
        } else {
            finalPath = largeImagePathI;
            Log.d("ggg", "dateINTERNAL = " + dateINTERNAL);
        }
        uri = Uri.fromFile(new File(finalPath));
        Picasso.with(this).load(uri).into(imageView);

        Log.d("ggg", "dateINTERNAL = " + dateINTERNAL + ", dateEXTERNAL = " + dateEXTERNAL);
    }

    @Override
    public void choiceForPhoto(boolean bool) {
        if (bool) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 111);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_CAPTURE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("finalPath", finalPath);
        outState.putInt("idList", idList);
    }
}
