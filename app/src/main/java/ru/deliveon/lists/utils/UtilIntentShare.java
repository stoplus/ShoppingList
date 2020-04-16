package ru.deliveon.lists.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ru.deliveon.lists.Products;
import ru.deliveon.lists.R;
import ru.deliveon.lists.database.entity.Lists;

public class UtilIntentShare {

    /**
     * Полелиться текстовым сообщением, возможно с заголовком
     *
     * @param dialogTitle Заголовок диалога
     * @param subject Текст заголовка сообщения для почты
     * @param message Текст сообщения
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


    /**
     * Поделиться файлом
     * @param file Файл, сохраненный на устройстве
     */
    public static void shareFile(Activity activity, File file, String mime) {
        /*Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        intentShareFile.setType(URLConnection.guessContentTypeFromName(file.getName()));
        intentShareFile.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName(), file));

        startActivity(Intent.createChooser(intentShareFile, "Share File"));*/

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        //intent.setDataAndType(FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName(), file), "application/excel");
        intent.setType(mime);
        intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName(), file));

        PackageManager pm = activity.getPackageManager();
        if (intent.resolveActivity(pm) != null) {
            activity.startActivity(intent);
        }



        /*Intent intent = new Intent(Intent.ACTION_SEND);
        String mimetype = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).getEncodedPath());
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());

        //intent.setData(FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName(), file))
        intent.setDataAndType(FileProvider.getUriForFile(activity, "ru.mts.money", file), "text/plain");
        //intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+  file.getAbsolutePath()));

        PackageManager pm = activity.getPackageManager();
        if (intent.resolveActivity(pm) != null) {
            startActivity(intent);
        }*/
    }

    private static String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }

    private static void saveFileTickets(Context context, Lists lotteryModel) {
        try {
            File folder = new File(context.getFilesDir(), context.getResources().getString(R.string.app_name));
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File outFile = new File(folder, "${DateUtils.nameTimeString()}.data");
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(lotteryModel);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Lists loadLotteryModel(Context context, String name) {
        Lists lists = null;
        try {
            String path = new File(context.getFilesDir(), context.getResources().getString(R.string.app_name)).toString();
            ObjectInputStream inn = new ObjectInputStream(new FileInputStream(path + "/" + name));
            lists = (Lists) inn.readObject();
            inn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lists;
    }
}
