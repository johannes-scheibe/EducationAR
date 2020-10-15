package com.example.educationar.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class FileManager {

    private static Logger logger = Logger.getLogger("EduAR-FileManager");

    public static void saveBitmap(Context context, Uri uri, Bitmap bitmap){
        OutputStream outputStream;
        try {
            outputStream = context.getContentResolver().openOutputStream(uri);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);

            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void transferToInternalStorage(Context context, Uri src, String fname) throws IOException{
        FileOutputStream fos = context.openFileOutput(fname, Context.MODE_PRIVATE);

        InputStream is = context.getContentResolver().openInputStream(src);
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            len = is.read(buffer);
            while (len != -1) {
                fos.write(buffer, 0, len);
                len = is.read(buffer);
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Print content of Internal Storage
    private static void printInternalStorage(Context context){
        logger.log(Level.INFO, "Internal storage contains: ");
        File mydir = context.getFilesDir();
        File lister = mydir.getAbsoluteFile();
        for (String list : lister.list())
        {
            logger.log(Level.INFO, "File: " +list);
        }
    }

    private static boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    // Checks if a volume containing external storage is available to at least read.
    private static boolean isExternalStorageReadable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ||
                Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }

}
