package com.example.educationar.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextureLoader {

    private static Logger logger = Logger.getLogger("EduAR-TextureLoader");

    public static int loadTexture(final Context context, final String file)
    {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        try{
            if (textureHandle[0] == 0)
            {
                throw new RuntimeException("Error generating texture name.");
            }

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open(file));
            logger.log(Level.INFO, "" + file);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_MIRRORED_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_MIRRORED_REPEAT);
            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }catch(IOException e){
            logger.log(Level.WARNING, "Texture loading failed due a IO Exception:" + e.getMessage());
        }

        logger.log(Level.INFO, "Texture loading finished:" + textureHandle[0]);
        return textureHandle[0];

    }
}
