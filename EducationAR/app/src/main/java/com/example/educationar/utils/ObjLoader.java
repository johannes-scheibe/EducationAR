package com.example.educationar.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.example.educationar.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ObjLoader {

    private static Logger logger = Logger.getLogger("EduAR-ObjLoader");

    public float vertices[];
    public float normals[];
    public float textures[];
    public float colors[];


    public ObjLoader(Context context, String file) {

        short vertexIndices[];
        short textureIndices[];
        short normalIndices[];

        int numFaces;
        List<Float> vertices = new ArrayList<Float>();
        List<Float> normals = new ArrayList<Float>();
        List<Float> textures = new ArrayList<Float>();

        List<String> faces = new ArrayList<>();

        BufferedReader reader = null;

        try {
            InputStreamReader in = new InputStreamReader(context.getAssets().open(file));
            reader = new BufferedReader(in);

            // read file until EOF
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                switch (parts[0]) {
                    case "v":
                        // vertices
                        vertices.add(Float.valueOf(parts[1]).floatValue()); // Opengl x-Coordinate = Blender x-Coordinate
                        vertices.add(-Float.valueOf(parts[3]).floatValue()); //Opengl y-Coordinate = - Blender z-Coordinate
                        vertices.add(Float.valueOf(parts[2]).floatValue()); // Opengl z-Coordinate = Blender y-Coordinate
                        break;
                    case "vn":
                        // normals
                        normals.add(Float.valueOf(parts[1]).floatValue());
                        normals.add(-Float.valueOf(parts[3]).floatValue());
                        normals.add(Float.valueOf(parts[2]).floatValue());
                        break;
                    case "vt":
                        // textures
                        textures.add(Float.valueOf(parts[1]).floatValue());
                        textures.add(1-Float.valueOf(parts[2]).floatValue());
                        break;
                    case "f":
                        // faces: vertex/texture/normal
                        faces.add(parts[1]);
                        faces.add(parts[2]);
                        faces.add(parts[3]);
                        break;

                }
            }


            // Create indices
            vertexIndices = new short[faces.size()];
            textureIndices = new short[faces.size()];
            normalIndices = new short[faces.size()];

            for (int i = 0; i < faces.size(); i++) {
                String[] parts = faces.get(i).split("/");
                logger.log(Level.INFO, faces.get(i));
                // Vertex index
                short vIndex = Short.valueOf(parts[0]).shortValue();
                vertexIndices[i] = (vIndex -= 1);
                logger.log(Level.INFO,""+vIndex);
                // Texture index
                short tIndex = Short.valueOf(parts[1]).shortValue();
                textureIndices[i] = (tIndex -= 1);
                // Normal index
                short nIndex = Short.valueOf(parts[2]).shortValue();
                normalIndices[i] = (nIndex -= 1);
            }


            int i;
            float[] vArray = new float[vertexIndices.length*3];
            i = 0;
            for (Short index : vertexIndices) {
                vArray[i++] = vertices.get(index*3);
                vArray[i++] = vertices.get(index*3+1);
                vArray[i++] = vertices.get(index*3+2);
            }
            this.vertices = vArray;

            float[] tArray = new float[textureIndices.length*2];
            i = 0;
            for (Short index : textureIndices) {
                tArray[i++] = textures.get(index*2);
                tArray[i++] = textures.get(index*2+1);
            }
            this.textures = tArray;

            float[] nArray = new float[normalIndices.length*3];
            i = 0;
            for (Short index : normalIndices) {
                nArray[i++] = normals.get(index*3);
                nArray[i++] = normals.get(index*3+1);
                nArray[i++] = normals.get(index*3+2);
            }
            this.normals = nArray;


            colors = new float[faces.size()*4];
            i =0;
            while(i<colors.length){
                colors[i++] = 1.0f;
                colors[i++] = 1.0f;
                colors[i++] = 1.0f;
                colors[i++] = 1.0f;
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
    }
}