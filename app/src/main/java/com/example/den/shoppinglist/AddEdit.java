package com.example.den.shoppinglist;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
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

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.den.shoppinglist.dialogs.CameraOrGalery;
import com.example.den.shoppinglist.entity.Product;
import com.example.den.shoppinglist.interfaces.CameraOrGaleryInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private final int CAMERA_CAPTURE = 2000;
    private final int REQUEST_PERMITIONS = 1100;
    private final int START_DIALOG_CHOICE_PHOTO = 200;
    private final int START_CONTEXT_MENU = 201;
    private View view;
    private int idList;
    private Product productReceived;
    private String linkNewPicture = "";
    private String finalPath = "";
    private boolean newImageFlag;
    private View.OnClickListener clickListener;
    private int dateAdded;
    String mCurrentPhotoPath;

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
        productReceived = getIntent().getParcelableExtra("product");
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
                    pathForGlide = Uri.parse(productReceived.getPictureLink());
                    if (newImageFlag) pathForGlide = createPathForGlide();
                    installListenerPhoto(START_CONTEXT_MENU);
                    break;
                case 2:
                    pathForGlide = Uri.parse(productReceived.getPictureLink());
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
                pathForGlide = Uri.parse(finalPath);
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
                //create new
                intent.putExtra("product", new Product(name, path, false, camera));
            } else {
                //update
                productReceived.setNameProduct(name);
                productReceived.setPictureLink(path);
                productReceived.setCamera(camera);
                intent.putExtra("productUpdate", productReceived);
            }
            setResult(RESULT_OK, intent);//return the result
            finish();
        } else {
            Snackbar.make(view, getResources().getString(R.string.enter_name_product), Snackbar.LENGTH_LONG).show();
        }
    }//add

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("AddEditclass", "requestCode = " + requestCode + "resultCode = " + resultCode + "data.getData = " + data);
        imageView.setOnClickListener(clickListener);
        camera = 0;
        switch (requestCode) {
            case CAMERA_CAPTURE://reqCode camera
                if (resultCode == RESULT_OK) {
                    // Show the thumbnail on ImageView
                    Uri imageUri = Uri.parse(mCurrentPhotoPath);
                    linkNewPicture = finalPath = String.valueOf(imageUri);
                    camera = 2;
                    newImageFlag = true;

                    if (productReceived != null && (productReceived.getPictureLink().isEmpty() || camera != productReceived.getCamera())) {
                        productReceived.setPictureLink(finalPath);
                        productReceived.setCamera(camera);
                    }
                    chekPerm();
                } else
                    Toast.makeText(this, getResources().getString(R.string.not_take_photo), Toast.LENGTH_LONG).show();
                break;
            default:
                Snackbar.make(view, "Сработал default!!!!!", Snackbar.LENGTH_SHORT).show();
                break;
            case 111:// reqCode system when selecting images
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

    @Override
    public void choiceForPhoto(boolean bool) {
        if (bool) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 111);
        } else {
            //Intention to start the camera
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    return;
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {

                    Uri photoURI = null;
                    if (Build.VERSION.SDK_INT < 24) {
                        photoURI = Uri.fromFile(photoFile);
                    } else {
                        photoURI = FileProvider.getUriForFile(AddEdit.this,
                                BuildConfig.APPLICATION_ID + ".provider",
                                photoFile);
                    }

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, CAMERA_CAPTURE);
                }
            } else Snackbar.make(view, getResources().getString(R.string.device_no_camera),
                    Snackbar.LENGTH_SHORT).show();
        }//if
    }//choiceForPhoto


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void installListenerPhoto(final int way) {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (way == START_DIALOG_CHOICE_PHOTO) {
                        selectWayForLoadPhoto();
                    } else if (way == START_CONTEXT_MENU) {
                        PopupMenu popup = new PopupMenu(v.getContext(), v, Gravity.CENTER);//create the menu window object
                        popup.inflate(R.menu.click_foto_menu);//inflate a menu from an XML file
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {//define the clicks on the menu items
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
                        popup.show();//show the menu window
                    }
                } catch (IndexOutOfBoundsException e) {
                    Log.d("AddEditclass", e.getMessage());
                }
            }//onClick
        };
    }//installListenerPhoto

    private void setPhoto(Uri uri, int errorPhoto) {
        GlideApp.with(AddEdit.this)
                .load(uri)
                .override(600, 600)
                .fitCenter()
                .error(errorPhoto)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
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

    //refund after agreement / denial of the user
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
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.attention))
                .setIcon(R.mipmap.warning)
                .setMessage(getResources().getString(R.string.need_get_permissions))
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .show();
        dialog.setCancelable(false);
    }

    @OnShowRationale({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    void showRationaleForCamera(final PermissionRequest request) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.need_obtain_permissions))
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
        dialog.setCancelable(false);
    }
}
