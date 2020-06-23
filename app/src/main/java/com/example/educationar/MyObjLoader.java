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
    short indices[];


    public MyObjLoader(String file) {

        int numFaces;
        List<Float> vertices = new ArrayList<Float>();

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
                    case "f":
                        // faces: vertex/texture/normal
                        faces.add(parts[1]);
                        faces.add(parts[2]);
                        faces.add(parts[3]);
                        break;

                }
            }

            indices = new short[faces.size()];

            for (int i = 0; i<faces.size(); i++) {
                String[] parts = faces.get(i).split("/");
                short value = Short.valueOf(parts[0]).shortValue();
                indices[i] = (value-=1);
            }

            float[] arr = new float[vertices.size()];
            int i = 0;
            for (Float value: vertices) {
                arr[i++] = value;
            }
            this.vertices = arr;

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