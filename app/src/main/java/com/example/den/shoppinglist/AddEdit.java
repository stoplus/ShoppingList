package com.example.den.shoppinglist;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.den.shoppinglist.dialogs.CameraOrGalery;
import com.example.den.shoppinglist.entity.Product;
import com.example.den.shoppinglist.interfaces.CameraOrGaleryInterface;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

import android.support.annotation.NonNull;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class AddEdit extends AppCompatActivity implements CameraOrGaleryInterface {
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
    private int camera;
    private final int CAMERA_CAPTURE = 1;
    private View view;
    private String finalPath = "";  //путь к файлу
    private Uri uri;
    private int idList;
    private final int REQUEST_PERMITIONS = 1100;
    private Product productReceived;
    private String linkNewPicture = "";
    private boolean bought = false;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_image);
        ButterKnife.bind(this);

        AddEditPermissionsDispatcher.chekPermWithPermissionCheck(AddEdit.this);
    }//onCreate


    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    void chekPerm() {
        productReceived = getIntent().getParcelableExtra("product");//для обновления
        idList = getIntent().getIntExtra("idList", -1);

        if (productReceived == null) {
            //новый продукт
            editText.setHint("Название");
//            title = "Создание нового товара";
            btnAdd.setText(getResources().getString(R.string.add));
            btnAddPhoto.setText(getResources().getString(R.string.addPhoto));
        } else {

            //обновляем
            editText.setText(productReceived.getNameProduct());
//            title = "Изменение названия товара";
            btnAddPhoto.setText(getResources().getString(R.string.editPhoto));
            btnAdd.setText(getResources().getString(R.string.edit));
//если фото с гплереи
            if (productReceived.getCamera() == 1) {

                Glide.with(AddEdit.this)
//                        .load(Uri.fromFile(new File(linkNewPicture)))
                        .load(Uri.parse(productReceived.getPictureLink()))
                        .override(300, 300)
                        .fitCenter()
                        .error(R.mipmap.ic_launcher_round)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
            }//if
            //если фото с камеры
            if (productReceived.getCamera() == 2) {
                Glide.with(AddEdit.this)
                        .load(Uri.parse(productReceived.getPictureLink()))
                        .override(300, 300)
                        .fitCenter()
                        .error(R.mipmap.ic_launcher_round)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
            }//if

            if (productReceived != null && !productReceived.getPictureLink().isEmpty()) {
                camera = productReceived.getCamera();
                bought = productReceived.isBought();
            }
        }//if
    }

    public void cancel(View view) {
        Intent intent = new Intent(AddEdit.this, Products.class);
        intent.putExtra("idList", idList);
        setResult(RESULT_CANCELED, intent);//возращаем результат
        finish();
    }


    public void addPhoto(View view) {
        try {// Намерение для запуска камеры
            CameraOrGalery cameraOrGalery = new CameraOrGalery();
            cameraOrGalery.show(getSupportFragmentManager(), "cameraOrGalery");
        } catch (ActivityNotFoundException e) {
            // Выводим сообщение об ошибке
            Snackbar.make(view, "Ваше устройство не поддерживает съемку", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void add(View view) {
        Intent intent = new Intent();
        name = editText.getText().toString();
        if (!name.isEmpty()) {
            if (productReceived == null) {
                //создаем новый
                Product product = new Product();
                product.setNameProduct(name);
                product.setBought(false);
                product.setCamera(camera);
                if (productReceived == null) {
                    product.setPictureLink(linkNewPicture);
                } else
                    product.setPictureLink(productReceived.getPictureLink());
                intent.putExtra("product", product);
            } else {
                //обновляем
                productReceived.setNameProduct(name);
                productReceived.setPictureLink(linkNewPicture);
                productReceived.setCamera(camera);
                productReceived.setBought(bought);
                intent.putExtra("productUpdate", productReceived);
            }
        } else {
            //Snackbar.make(view, "Введите название покупки", Snackbar.LENGTH_SHORT).show();
            Toast.makeText(AddEdit.this, "Введите название покупки", Toast.LENGTH_SHORT).show();
        }
        setResult(RESULT_OK, intent);//возращаем результат
        finish();
    }//add


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        camera = 0;
        switch (requestCode) {
            case CAMERA_CAPTURE://reqCode авторизации в ВК
                if (resultCode == Activity.RESULT_OK) {
                    createPathPhoto();
                } else Toast.makeText(this, "Вы не сделали фото", Toast.LENGTH_LONG).show();
                break;
            case 111://reqCode системы при выборе картинок
                if (resultCode == Activity.RESULT_OK) {
                    uri = data.getData();
                    linkNewPicture = finalPath = String.valueOf(uri);
                    Uri fff = Uri.parse(finalPath);
                    Glide.with(AddEdit.this)
                            .load(fff)
                            .override(300, 300)
                            .fitCenter()
                            .error(R.mipmap.ic_launcher_round)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imageView);

//                    Picasso.with(this).load(fff).resize(250, 250).into(imageView);
                    camera = 1;
                } else Toast.makeText(this, "Вы не выбрали фото", Toast.LENGTH_LONG).show();
                break;
            case REQUEST_PERMITIONS:
                AddEditPermissionsDispatcher.chekPermWithPermissionCheck(AddEdit.this);
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
        } else {
            finalPath = largeImagePathI;
        }
        uri = Uri.fromFile(new File(finalPath));

        linkNewPicture = String.valueOf(uri);
        Uri fff = Uri.parse(linkNewPicture);
        Glide.with(AddEdit.this)
                .load(fff)
                .override(300, 300)
                .fitCenter()
                .error(R.mipmap.ic_launcher_round)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
        camera = 2;
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
        outState.putInt("camera", camera);
        outState.putParcelable("productReceived", productReceived);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        finalPath = savedInstanceState.getString("finalPath");
        idList = savedInstanceState.getInt("idList");
        camera = savedInstanceState.getInt("camera");
        productReceived = savedInstanceState.getParcelable("productReceived");
    }

    //=========================================================================================================
    //возврат после соглашения/отказа пользователя
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AddEditPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }//onRequestPermissionsResult

    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    void permissionsDenied() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_PERMITIONS);
    }//permissionsDenied

    @OnNeverAskAgain({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    void onNeverAskAgain() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Title")
                .setMessage("Message")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    @OnShowRationale({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    void showRationaleForCamera(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("Это надо вам!!!")
                .setPositiveButton("хорошо", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton("низачто", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }
}
