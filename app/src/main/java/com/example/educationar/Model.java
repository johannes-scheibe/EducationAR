package com.example.educationar;

import android.content.Context;

import com.example.educationar.rendering.MyShaderProgram;
import com.example.educationar.rendering.ShaderProgram;

import org.artoolkitx.arx.arxj.rendering.ARDrawable;
import org.artoolkitx.arx.arxj.rendering.RenderUtils;


import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Model {
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mColorBuffer;
    private ShortBuffer mIndexBuffer;
    private ShaderProgram shaderProgram;


    private static Logger logger = Logger.getLogger("EducationAR-Model");

    public Model(Context context,  String file) {

        MyObjLoader objLoader = new MyObjLoader( file);

        float x = 0.0f;
        float y = 0.0f;
        float z = 20.0f;
        float hs = 40.0f / 2.0f;

        float vertices[] = {
                x - hs, y - hs, z - hs, // 0 --> If you look at the cube from the front, this is the corner
                // in the front on the left of the ground plane.
                x + hs, y - hs, z - hs, // 1 --> That is the one to the right of corner 0
                x + hs, y + hs, z - hs, // 2 --> That is the one to the back right of corner 0
                x - hs, y + hs, z - hs, // 3 --> That is the one to the left of corner 2
                // Or if you imaging (or paint) a 3D cube on paper this is the only corner that is hidden
                x - hs, y - hs, z + hs, // 4 --> That is the top left corner. Directly on top of 0
                x + hs, y - hs, z + hs, // 5 --> That is directly on top of 1
                x + hs, y + hs, z + hs, // 6 --> That is directly on top of 2
                x - hs, y + hs, z + hs, // 7 --> That is directly on top of 3
        };
        float c = 1.0f;
        float colors[] = {
                0, 0, 0, c, // 0 black
                c, 0, 0, c, // 1 red
                c, c, 0, c, // 2 yellow
                0, c, 0, c, // 3 green
                0, 0, c, c, // 4 blue
                c, 0, c, c, // 5 magenta
                c, c, c, c, // 6 white
                0, c, c, c, // 7 cyan
        };

        byte indices[] = {
                // bottom
                1, 0, 2,
                2, 0, 3,
                // right
                1, 2, 5,
                5, 2, 6,
                // top
                4, 5, 7,
                7, 5, 6,
                // left
                0, 4, 3,
                3, 4, 7,
                // back
                7, 6, 3,
                6, 2, 3,
                // front
                0, 1, 4,
                4, 1, 5
        };

        mVertexBuffer = RenderUtils.buildFloatBuffer(objLoader.vertices);
        mColorBuffer = null;
        mIndexBuffer = RenderUtils.buildShortBuffer(objLoader.indices);
    }

    @SuppressWarnings("WeakerAccess")
    public FloatBuffer getmVertexBuffer() {
        return mVertexBuffer;
    }
    @SuppressWarnings("WeakerAccess")
    public FloatBuffer getmColorBuffer() {
        return mColorBuffer;
    }
    @SuppressWarnings("WeakerAccess")
    public ShortBuffer getmIndexBuffer() {
        return mIndexBuffer;
    }

    /*
     * Used to render objects when working with OpenGL ES 2.x
     *
     * @param projectionMatrix The projection matrix obtained from the ARToolkit
     * @param modelViewMatrix  The marker transformation matrix obtained from ARToolkit
     */
    public void draw(float[] projectionMatrix, float[] modelViewMatrix) {

        shaderProgram.setProjectionMatrix(projectionMatrix);
        shaderProgram.setModelViewMatrix(modelViewMatrix);

        shaderProgram.render(this.getmVertexBuffer(), this.getmColorBuffer(), this.getmIndexBuffer());

    }

    /*
     * Sets the shader program used by this geometry.
     */
    public void setShaderProgram(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }
}
