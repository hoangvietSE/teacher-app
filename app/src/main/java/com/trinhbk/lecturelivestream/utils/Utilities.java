package com.trinhbk.lecturelivestream.utils;

import android.content.Context;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by TrinhBK on 10/9/2018.
 */

public class Utilities {

    public static File fileSave = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/LectureLiveStream");

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static File saveImage(String nameOfImage, String imageData) {
        if (!fileSave.exists()) {
            fileSave.mkdirs();
        }
        byte[] imgBytesData = android.util.Base64.decode(imageData, android.util.Base64.DEFAULT);

//        File file = null;
//        try {
//            file = File.createTempFile(nameOfImage, ".png", fileSave);
        File file = new File(fileSave, nameOfImage);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        try {
            bufferedOutputStream.write(imgBytesData);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                bufferedOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
