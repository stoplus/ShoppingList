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
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
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
    @BindView(R.id.idCancel)
    Button btnCancel;
    @BindView(R.id.idAdd)
    Button btnAdd;
    @BindView(R.id.idLayoutBtn)
    LinearLayout layoutBtn;
    private int camera;
    private final int CAMERA_CAPTURE = 1;
    private final int REQUEST_PERMITIONS = 1100;
    private final int START_DIALOG_CHOICE_PHOTO = 200;
    private final int START_CONTEXT_MENU = 201;
    private View view;
    private int idList;
    private Product productReceived;
    private String linkNewPicture = "";
    private String finalPath = "";  //путь к файлу
    private boolean newImageFlag;
    private View.OnClickListener clickListener;
    private int dateAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = getLayoutInflater().inflate(R.layout.activity_camera_image, null);
        setContentView(view);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            newImageFlag = savedInstanceState.getBoolean("newImageFlag");
            finalPath = savedInstanceState.getString("finalPath");
            linkNewPicture = savedInstanceState.getString("linkNewPicture");
            idList = savedInstanceState.getInt("idList");
            camera = savedInstanceState.getInt("camera");
            productReceived = savedInstanceState.getParcelable("productReceived");
        }//if savedInstanceState

        AddEditPermissionsDispatcher.chekPermWithPermissionCheck(AddEdit.this);
    }//onCreate


    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    void chekPerm() {
        productReceived = getIntent().getParcelableExtra("product");//для обновления
        idList = getIntent().getIntExtra("idList", -1);
        Uri pathForGlide = null;
        int idErrorPhoto;

        if (!newImageFlag) newImageFlag = getIntent().getBooleanExtra("newImageFlag", false);

        if (productReceived == null) {//new product
            idErrorPhoto = R.mipmap.default_photo;
            btnAdd.setText(getResources().getString(R.string.add));
            pathForGlide = createPathForGlide();
            if (linkNewPicture.isEmpty()) installListenerPhoto(START_DIALOG_CHOICE_PHOTO);
            else installListenerPhoto(START_CONTEXT_MENU);
        } else {//update product
            editText.setText(productReceived.getNameProduct());
            btnAdd.setText(getResources().getString(R.string.edit));
            idErrorPhoto = R.mipmap.no_photo;

            switch (productReceived.getCamera()) {
                case 1:
                    pathForGlide = Uri.parse(productReceived.getPictureLink());//без поворота и при обычном повороте
                    if (newImageFlag) pathForGlide = createPathForGlide();
                    installListenerPhoto(START_CONTEXT_MENU);
                    break;
                case 2:
                    pathForGlide = Uri.fromFile(new File(productReceived.getPictureLink()));//без поворота и при обычном повороте
                    if (newImageFlag) pathForGlide = createPathForGlide();
                    installListenerPhoto(START_CONTEXT_MENU);
                    break;
                case 0:
                    idErrorPhoto = R.mipmap.default_photo;
                    installListenerPhoto(START_DIALOG_CHOICE_PHOTO);
                    break;
            }//switch
            // TODO: 23.05.2018
            if (productReceived != null) camera = productReceived.getCamera();
        }//if
        setPhoto(pathForGlide, idErrorPhoto);
        imageView.setOnClickListener(clickListener);
    }//chekPerm

    private Uri createPathForGlide() {
        Uri pathForGlide = null;
        switch (camera) {
            case 1:
                pathForGlide = Uri.parse(finalPath);
                break;
            case 2:
                pathForGlide = Uri.fromFile(new File(finalPath));
                break;
        }//switch
        return pathForGlide;
    }//createPathForGlide

    public void cancel(View view) {
        Intent intent = new Intent(AddEdit.this, Products.class);
        intent.putExtra("idList", idList);
        setResult(RESULT_CANCELED, intent);//возращаем результат
        finish();
    }//cancel

    public void selectWayForLoadPhoto() {
        CameraOrGalery cameraOrGalery = new CameraOrGalery();
        cameraOrGalery.show(getSupportFragmentManager(), "cameraOrGalery");
    }//selectWayForLoadPhoto

    public void add(View view) {
        Intent intent = new Intent();
        String name = editText.getText().toString();
        if (!name.isEmpty()) {
            String path = linkNewPicture;//without turning and during a normal coup
            if (newImageFlag) path = finalPath;//Only when you rotate with a new picture

            if (path.isEmpty()) {
                if (productReceived != null)
                    path = productReceived.pictureLink;//if the picture remains the same
                else path = "";
            }

            if (productReceived == null) {
                //создаем новый
                Product product = new Product();
                product.setNameProduct(name);
                product.setBought(false);
                product.setCamera(camera);
                product.setPictureLink(path);
                intent.putExtra("product", product);
            } else {
                //обновляем
                productReceived.setNameProduct(name);
                productReceived.setPictureLink(path);
                productReceived.setCamera(camera);
                intent.putExtra("productUpdate", productReceived);
            }
            setResult(RESULT_OK, intent);//возращаем результат
            finish();
        } else {
            Snackbar.make(view, "Введите название покупки", Snackbar.LENGTH_LONG).show();
        }
    }//add

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        imageView.setOnClickListener(clickListener);
        camera = 0;
        switch (requestCode) {
            case CAMERA_CAPTURE://reqCode камеры
                if (resultCode == Activity.RESULT_OK) {
                    createPathPhoto();
                    if (productReceived != null && (productReceived.getPictureLink().isEmpty() || camera != productReceived.getCamera())) {
                        productReceived.setPictureLink(finalPath);
                        productReceived.setCamera(camera);
                    }
                    chekPerm();
                } else
                    Toast.makeText(this, getResources().getString(R.string.not_take_photo), Toast.LENGTH_LONG).show();
                break;
            case 111://reqCode системы при выборе картинок
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    linkNewPicture = finalPath = String.valueOf(uri);
                    camera = 1;
                    newImageFlag = true;
                    if (productReceived != null && (productReceived.getPictureLink().isEmpty() || camera != productReceived.getCamera())) {
                        productReceived.setPictureLink(finalPath);
                        productReceived.setCamera(camera);
                    }
                    chekPerm();
                } else
                    Toast.makeText(this, getResources().getString(R.string.not_selected_photo), Toast.LENGTH_LONG).show();
                break;
            case REQUEST_PERMITIONS:
                AddEditPermissionsDispatcher.chekPermWithPermissionCheck(AddEdit.this);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }//onActivityResult

    private void createPathPhoto() {
        String largeImagePathE = getLastCreatedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Log.d("AddEditclass", "largeImagePathE = "+ largeImagePathE);
        int dateAddedEXTERNAL = dateAdded;
        Log.d("AddEditclass", "dateAddedEXTERNAL = "+ dateAddedEXTERNAL);

        String largeImagePathI = getLastCreatedPath(MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        Log.d("AddEditclass", "largeImagePathI = "+ largeImagePathI);
        int dateAddedINTERNAL = dateAdded;
        Log.d("AddEditclass", "dateAddedINTERNAL = "+ dateAddedINTERNAL);

        if (dateAddedEXTERNAL > dateAddedINTERNAL) {
            finalPath = largeImagePathE;
        } else {
            finalPath = largeImagePathI;
        }
        Uri uri = Uri.fromFile(new File(finalPath));
        linkNewPicture = String.valueOf(uri);
        camera = 2;
        newImageFlag = true;
    }//createPathPhoto

    public String getLastCreatedPath(Uri internalContentUri) {
        dateAdded = 0;
        String[] fileProjection = {
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.DATE_ADDED
        };
        String fileSort = MediaStore.Images.ImageColumns._ID + " DESC";

        String path = "";
        try (Cursor myCursorLargeE = getContentResolver().query(
                internalContentUri,
                fileProjection,
                null,
                null,
                fileSort)) {
            if (myCursorLargeE != null && myCursorLargeE.getCount() > 0) {
                myCursorLargeE.moveToFirst();
                dateAdded = Integer.parseInt(myCursorLargeE.getString(myCursorLargeE.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED)));
                path = myCursorLargeE.getString(myCursorLargeE.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
            }
        }
        return path;
    }

    @Override
    public void choiceForPhoto(boolean bool) {
        if (bool) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 111);
        } else {
            try {//Intention to start the camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_CAPTURE);
            } catch (ActivityNotFoundException e) {
                Snackbar.make(view, getResources().getString(R.string.device_no_camera),
                        Snackbar.LENGTH_SHORT).show();
            }
        }//if
    }//choiceForPhoto

    private void installListenerPhoto(final int way) {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (way == START_DIALOG_CHOICE_PHOTO) {
                        selectWayForLoadPhoto();
                    } else if (way == START_CONTEXT_MENU) {
                        PopupMenu popup = new PopupMenu(v.getContext(), v, Gravity.CENTER);//создаем объект окна меню
                        popup.inflate(R.menu.click_foto_menu);//закачиваем меню из XML файла
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {//определяем нажатия на элементы меню
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.change_photo:
                                        selectWayForLoadPhoto();
                                        return true;
                                    case R.id.delete_photo:
                                        camera = 0;
                                        finalPath = "";
                                        linkNewPicture = "";
                                        newImageFlag = false;
                                        if (productReceived != null) {
                                            productReceived.setPictureLink(finalPath);
                                            productReceived.setCamera(camera);
                                        }
                                        chekPerm();
                                        return true;
                                    default:
                                        break;
                                }//switch
                                return false;
                            }//onMenuItemClick
                        });
                        popup.show();//показываем окно меню
                    }
                } catch (IndexOutOfBoundsException e) {
                    Log.d("AddEdit", e.getMessage());
                }
            }
        };
    }//installListenerPhoto

    private void setPhoto(Uri uri, int errorPhoto) {
        Glide.with(AddEdit.this)
                .load(uri)
                .override(600, 600)
                .fitCenter()
                .error(errorPhoto)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(imageView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("finalPath", finalPath);
        outState.putInt("idList", idList);
        outState.putInt("camera", camera);
        outState.putBoolean("newImageFlag", newImageFlag);
        outState.putParcelable("productReceived", productReceived);
        outState.putString("linkNewPicture", linkNewPicture);
    }//onSaveInstanceState

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        finalPath = savedInstanceState.getString("finalPath");
        linkNewPicture = savedInstanceState.getString("linkNewPicture");
        idList = savedInstanceState.getInt("idList");
        camera = savedInstanceState.getInt("camera");
        productReceived = savedInstanceState.getParcelable("productReceived");
        newImageFlag = savedInstanceState.getBoolean("newImageFlag");
    }//onRestoreInstanceState

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
        AlertDialog dialog = new AlertDialog.Builder(this)
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
        dialog.setCancelable(false);
    }
}
