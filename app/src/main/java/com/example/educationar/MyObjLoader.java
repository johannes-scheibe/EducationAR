package com.example.educationar;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MyObjLoader {

    private static Logger logger = Logger.getLogger("EducationAR-ObjLoader");

    float vertices[];
    float normals[];
    float textures[];
    short vertexIndices[];
    short textureIndices[];
    short normalIndices[];


    public MyObjLoader(String file) {

        int numFaces;
        List<Float> vertices = new ArrayList<Float>();
        List<Float> normals = new ArrayList<Float>();
        List<Float> textures = new ArrayList<Float>();

        List<String> faces = new ArrayList<>();

        BufferedReader reader = null;

        try {
            InputStreamReader in = new InputStreamReader(MainActivity.getContext().getAssets().open(file));
            reader = new BufferedReader(in);

            // read file until EOF
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                switch (parts[0]) {
                    case "v":
                        // vertices
                        vertices.add(Float.valueOf(parts[1]).floatValue()); // x-Coordinate
                        vertices.add(Float.valueOf(parts[2]).floatValue()); // y-Coordinate
                        vertices.add(Float.valueOf(parts[3]).floatValue()); // z-Coordinate
                        break;
                    case "vn":
                        // normals
                        normals.add(Float.valueOf(parts[1]).floatValue());
                        normals.add(Float.valueOf(parts[2]).floatValue());
                        normals.add(Float.valueOf(parts[3]).floatValue());
                        break;
                    case "vt":
                        // textures
                        textures.add(Float.valueOf(parts[1]).floatValue());
                        textures.add(Float.valueOf(parts[2]).floatValue());
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
            for (int i = 0; i<faces.size(); i++) {
                String[] parts = faces.get(i).split("/");
                // Vertex index
                short vIndex = Short.valueOf(parts[0]).shortValue();
                vertexIndices[i] = (vIndex-=1);
                // Texture index
                short tIndex = Short.valueOf(parts[1]).shortValue();
                textureIndices[i] = (tIndex-=1);
                // Normal index
                short nIndex = Short.valueOf(parts[1]).shortValue();
                normalIndices[i] = (nIndex-=1);
            }

            // Convert ArrayLists to arrays
            int i;

            float[] vArray = new float[vertices.size()];
            i = 0;
            for (Float value: vertices) {
                vArray[i++] = value;
            }
            this.vertices = vArray;

            float[] tArray = new float[textures.size()];
            i = 0;
            for (Float value: textures) {
                tArray[i++] = value;
            }
            this.normals = tArray;

            float[] nArray = new float[normals.size()];
            i = 0;
            for (Float value: normals) {
                nArray[i++] = value;
            }
            this.normals = nArray;


        } catch (IOException e) {
            logger.log(Level.SEVERE,e.getMessage());
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