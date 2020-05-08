package ru.deliveon.lists.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.content.FileProvider;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.deliveon.lists.BuildConfig;
import ru.deliveon.lists.R;
import ru.deliveon.lists.entity.ExportList;

public class UtilIntentShare {
    private static final String DATE_FORMAT_NAME = "yyMMdd_HHmmss";

    /**
     * Полелиться текстовым сообщением, возможно с заголовком
     *
     * @param dialogTitle Заголовок диалога
     * @param subject     Текст заголовка сообщения для почты
     * @param message     Текст сообщения
     */
    public static void shareText(Context context, String dialogTitle, String subject, String message) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        if (subject != null) {
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        if (message != null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        }
        if (dialogTitle == null) {
            dialogTitle = "Поделиться";
        }
        context.startActivity(Intent.createChooser(shareIntent, dialogTitle));
    }


    public static void shareFile(Activity activity, String dialogTitle, String subject) {
        String f = new File(activity.getFilesDir(), activity.getResources().getString(R.string.app_name)).toString() + "/list_for_import.top";
        File file = new File(f);

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        intentShareFile.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intentShareFile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        if (subject != null) {
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        if (dialogTitle == null) {
            dialogTitle = "Экспорт списка";
        }
        intentShareFile.setType("text/plain");

        if (Build.VERSION.SDK_INT < 24) {
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        } else {
            intentShareFile.putExtra(Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", file));
        }

        activity.startActivity(Intent.createChooser(intentShareFile, dialogTitle));
    }

    public static boolean saveFileList(Context context, ExportList lotteryModel) {
        try {
            File folder = new File(context.getFilesDir(), context.getResources().getString(R.string.app_name));
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File outFile = new File(folder, "list_for_import.top");

            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(lotteryModel);
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String nameTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NAME, Locale.getDefault());
        return sdf.format(new Date());
    }

    public static String getRealPathFromURI(Activity activity, Uri contentURI) {
        String result;
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
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

    private static String getFileName(Activity activity, Uri uri) throws IllegalArgumentException {
        try (Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
            }
        }
        return null;
    }

    private static File copyToTempFile(Activity activity, Uri uri, File tempFile) throws IOException {
        // Obtain an input stream from the uri
        InputStream inputStream = activity.getContentResolver().openInputStream(uri);

        if (inputStream == null) {
            throw new IOException("Unable to obtain input stream from URI");
        }

        // Copy the stream to the temp file
        FileUtils.copyInputStreamToFile(inputStream, tempFile);
        return tempFile;
    }

    public static ExportList importFile(Activity activity, Uri uri) {
        String fileName = getFileName(activity, uri);
        File outputDir = activity.getCacheDir(); //путь к кеш папке приложения
        ExportList exportList = null;
        try {
            File tempFile = File.createTempFile("temp_file", fileName, outputDir);//создаем временный файл
            File fileCopy = copyToTempFile(activity, uri, tempFile);//копируем в него данные

            //пробуем привести данные временного файла к модели ExportList
            ObjectInputStream inn = new ObjectInputStream(new FileInputStream(fileCopy.getPath()));
            exportList = (ExportList) inn.readObject();
            inn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exportList;
    }
}
