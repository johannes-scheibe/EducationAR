package com.example.educationar.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ObjLoader {

    private static Logger logger = Logger.getLogger("EduAR-ObjLoader");

    public float vertices[];
    public float normals[];
    public float textures[];
    public float colors[];


    public ObjLoader(Context context, String file) {

        int vertexIndices[];
        int textureIndices[];
        int normalIndices[];

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
                        vertices.add(-Float.valueOf(parts[3]).floatValue()); // Opengl y-Coordinate = - Blender z-Coordinate
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
            vertexIndices = new int[faces.size()];
            textureIndices = new int[faces.size()];
            normalIndices = new int[faces.size()];

            for (int i = 0; i < faces.size(); i++) {
                String[] parts = faces.get(i).split("/");

                // Vertex index
                int vIndex = Integer.valueOf(parts[0]).intValue();
                vertexIndices[i] = (vIndex -= 1);

                // Texture index
                int tIndex = Integer.valueOf(parts[1]).intValue();
                textureIndices[i] = (tIndex -= 1);

                // Normal index
                int nIndex = Integer.valueOf(parts[2]).intValue();
                normalIndices[i] = (nIndex -= 1);

            }


            int i;
            this.vertices = new float[vertexIndices.length*3];
            i = 0;
            for (int index : vertexIndices) {
                this.vertices[i++] = vertices.get(index*3);
                this.vertices[i++] = vertices.get(index*3+1);
                this.vertices[i++] = vertices.get(index*3+2);
            }


            this.textures = new float[textureIndices.length*2];
            i = 0;
            for (int index : textureIndices) {
                this.textures[i++] = textures.get(index*2);
                this.textures[i++] = textures.get(index*2+1);
            }


            this.normals = new float[normalIndices.length*3];
            i = 0;
            for (int index : normalIndices) {
                this.normals[i++] = normals.get(index*3);
                this.normals[i++] = normals.get(index*3+1);
                this.normals[i++] = normals.get(index*3+2);
            }


            colors = new float[faces.size()*4];
            i = 0;
            while(i<colors.length){
                colors[i++] = 1.0f;
                colors[i++] = 1.0f;
                colors[i++] = 1.0f;
                colors[i++] = 1.0f;
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage() + ": ");
            e.printStackTrace();
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