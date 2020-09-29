package com.example.educationar.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MarkerGenerator {
    private static Logger logger = Logger.getLogger("EduAR-MarkerGenerator");

    public static Bitmap generateMarker(int id, int size){
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        char[] binary = getCodedID(id, (int) Math.pow(size,2)).toCharArray();

        // Create the standard marker
        for(int i = 0; i < bitmap.getWidth(); i++){
            for(int j = 0; j < bitmap.getHeight(); j++){
                int index = i * 5 + j;
                if(binary[index] == '1')
                    bitmap.setPixel(j, i, Color.BLACK);
                else
                    bitmap.setPixel(j, i, Color.WHITE);
            }
        }

        bitmap = Bitmap.createScaledBitmap(bitmap, 180, 180, false);
        return addBorder(bitmap,0.5);
    }

    public static Bitmap addBorder(Bitmap bmp, double borderSize){
        if (borderSize < 1){
            int borderPixels = (int) (bmp.getWidth() * borderSize);

            Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + borderPixels * 2, bmp.getHeight() + borderPixels * 2, bmp.getConfig());
            Canvas canvas = new Canvas(bmpWithBorder);
            canvas.drawColor(Color.BLACK);
            canvas.drawBitmap(bmp, borderPixels, borderPixels, null);
            return bmpWithBorder;
        }
        return bmp;
    }
    public static String getCodedID(int x, int len){
        // Convert the id to binary
        String binary = String.format("%" + (len-3) + "s", Integer.toBinaryString(x)).replace(" ", "0");

        // Insert ones for the left sided corners and a zero for the lower right corner (rotation invariance)
        binary = "1" + binary.substring(0,19) + "1" + binary.substring(19,22) + "0";

        logger.log(Level.INFO, "ID = " + binary);

        return binary;
    }

}
