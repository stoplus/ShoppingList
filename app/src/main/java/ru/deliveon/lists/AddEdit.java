package ru.deliveon.lists;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import ru.deliveon.lists.dialogs.CameraOrGalery;
import ru.deliveon.lists.database.entity.Product;
import ru.deliveon.lists.interfaces.CameraOrGaleryInterface;

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
    @BindView(R.id.hint_image)
    TextView hintImage;
    private final int CAMERA_CAPTURE = 2000;
    private final int REQUEST_PERMITIONS = 1100;
    private final int START_DIALOG_CHOICE_PHOTO = 200;
    private final int START_CONTEXT_MENU = 201;
    private View view;
    private int idList;
    private int sortNum;
    private Product productReceived;
    private String linkNewPicture = "";
    private String finalPath = "";
    private boolean newImageFlag;
    private View.OnClickListener clickListener;
    private String mCurrentPhotoPath;
    private PopupMenu popup;


    @SuppressLint("InflateParams")
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
            mCurrentPhotoPath = savedInstanceState.getString("mCurrentPhotoPath");
            idList = savedInstanceState.getInt("idList");
            sortNum = savedInstanceState.getInt("sortNum");
            productReceived = savedInstanceState.getParcelable("productReceived");
        }//if savedInstanceState

        AddEditPermissionsDispatcher.chekPermWithPermissionCheck(AddEdit.this);
    }//onCreate


    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    void chekPerm() {
        productReceived = getIntent().getParcelableExtra("product");
        idList = getIntent().getIntExtra("idList", -1);
        sortNum = getIntent().getIntExtra("sortNum", 0);
        Uri pathForGlide;
        int idErrorPhoto;

        if (!newImageFlag) newImageFlag = getIntent().getBooleanExtra("newImageFlag", false);

        if (productReceived == null) {//new product
            idErrorPhoto = R.drawable.default_photo;
            btnAdd.setText(getResources().getString(R.string.add));
            pathForGlide = Uri.parse(finalPath);
            if (linkNewPicture.isEmpty()) {
                installListenerPhoto(START_DIALOG_CHOICE_PHOTO);

            } else installListenerPhoto(START_CONTEXT_MENU);
        } else {//update product
            editText.setText(productReceived.getNameProduct());
            btnAdd.setText(getResources().getString(R.string.edit));
            idErrorPhoto = R.drawable.no_photo;
            pathForGlide = Uri.parse(productReceived.getPictureLink());
            if (newImageFlag) {
                pathForGlide = Uri.parse(finalPath);
                installListenerPhoto(START_CONTEXT_MENU);
            } else {
                if (productReceived.getPictureLink().isEmpty()) {
                    installListenerPhoto(START_DIALOG_CHOICE_PHOTO);
                    idErrorPhoto = R.drawable.default_photo;
                } else {
                    installListenerPhoto(START_CONTEXT_MENU);
                }
            }
        }//if

        setPhoto(pathForGlide, idErrorPhoto);
        imageView.setOnClickListener(clickListener);
    }//chekPerm


    private void setPhoto(Uri uri, int errorPhoto) {
        GlideApp.with(AddEdit.this)
                .load(uri)
                .override(600, 600)
                .fitCenter()
                .error(errorPhoto)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(imageView);
    }

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
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            String path = linkNewPicture;//without turning and during a normal coup
            if (newImageFlag) path = finalPath;//Only when you rotate with a new picture

            if (path.isEmpty()) {
                if (productReceived != null)
                    path = productReceived.pictureLink;//if the picture remains the same
                else path = "";
            }

            if (productReceived == null) {
                intent.putExtra("product", new Product(name, path, false, sortNum));
            } else {
                //update
                productReceived.setNameProduct(name);
                productReceived.setPictureLink(path);
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
        switch (requestCode) {
            case CAMERA_CAPTURE://reqCode camera
                if (resultCode == RESULT_OK) {
                    // Show the thumbnail on ImageView
                    Uri imageUri = Uri.parse(mCurrentPhotoPath);
                    linkNewPicture = finalPath = String.valueOf(imageUri);
                    newImageFlag = true;

                    if (productReceived != null && (productReceived.getPictureLink().isEmpty())) {
                        productReceived.setPictureLink(finalPath);
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

                    Uri uri;
                    if (Build.VERSION.SDK_INT < 24) {
                        uri = data.getData();
                    } else {
                        File photoFile = new File(getRealPathFromURI(data.getData()));
                        uri = FileProvider.getUriForFile(AddEdit.this,
                                BuildConfig.APPLICATION_ID + ".provider",
                                photoFile);
                    }

                    linkNewPicture = finalPath = String.valueOf(uri);
                    newImageFlag = true;
                    if (productReceived != null && (productReceived.getPictureLink().isEmpty())) {
                        productReceived.setPictureLink(finalPath);
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

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { //checking
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

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
                File photoFile;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    return;
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {

                    Uri photoURI;
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
        @SuppressLint("SimpleDateFormat")
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
        if (way == START_DIALOG_CHOICE_PHOTO) hintImage.setVisibility(View.VISIBLE);
        else hintImage.setVisibility(View.GONE);

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (way == START_DIALOG_CHOICE_PHOTO) {
                        selectWayForLoadPhoto();
                    } else if (way == START_CONTEXT_MENU) {
                        popup = new PopupMenu(AddEdit.this, editText, Gravity.CENTER_HORIZONTAL);//create the menu window object
                        popup.inflate(R.menu.click_foto_menu);//inflate a menu from an XML file
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {//define the clicks on the menu items
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.change_photo:
                                        selectWayForLoadPhoto();
                                        return true;
                                    case R.id.delete_photo:
                                        finalPath = "";
                                        linkNewPicture = "";
                                        newImageFlag = false;
                                        if (productReceived != null) {
                                            productReceived.setPictureLink(finalPath);
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


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("finalPath", finalPath);
        outState.putInt("idList", idList);
        outState.putInt("sortNum", sortNum);
        outState.putBoolean("newImageFlag", newImageFlag);
        outState.putParcelable("productReceived", productReceived);
        outState.putString("linkNewPicture", linkNewPicture);
        outState.putString("mCurrentPhotoPath", mCurrentPhotoPath);
    }//onSaveInstanceState


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
                .setIcon(R.drawable.warning)
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

    @Override
    protected void onPause() {
        if (popup != null) {
            popup.dismiss();
        }
        super.onPause();
    }
}
