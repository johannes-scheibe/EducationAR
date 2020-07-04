package com.example.educationar;

import android.content.Context;

import com.example.educationar.rendering.ShaderProgram;
import com.example.educationar.utils.ObjLoader;
import com.example.educationar.utils.TextureLoader;

import org.artoolkitx.arx.arxj.rendering.RenderUtils;


import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class Model {

    private static Logger logger = Logger.getLogger("EduAR-Model");

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mColorBuffer;
    private FloatBuffer mNormalBuffer;
    private FloatBuffer mTextureBuffer;
    private ShortBuffer mIndexBuffer;

    private ShaderProgram shaderProgram;

    // Load the Texture
    private int mTextureDataHandle;

    public Model(Context context,  String filename) {

        ObjLoader objLoader = new ObjLoader(MainActivity.getContext(), filename + ".obj");

        float c = 1.0f;
        float colorpalette[] = {
                0, 0, 0, c, // 0 black
                c, 0, 0, c, // 1 red
                c, c, 0, c, // 2 yellow
                0, c, 0, c, // 3 green
                0, 0, c, c, // 4 blue
                c, 0, c, c, // 5 magenta
                c, c, c, c, // 6 white
                0, c, c, c, // 7 cyan
        };

        // Add a random color to each vertex
        int colorlength = colorpalette.length;
        float[] colors = new float[objLoader.vertices.length];
        for(int i = 0; i<colors.length;i++){
            int rand = ThreadLocalRandom.current().nextInt(4,colorlength);
            colors[i] = colorpalette[rand];
        }

        mVertexBuffer = RenderUtils.buildFloatBuffer(objLoader.vertices);
        mColorBuffer = RenderUtils.buildFloatBuffer(colors);
        mNormalBuffer = RenderUtils.buildFloatBuffer(colors);
        mTextureBuffer = RenderUtils.buildFloatBuffer(objLoader.textures);
        mIndexBuffer = RenderUtils.buildShortBuffer(objLoader.vertexIndices);

        mTextureDataHandle = TextureLoader.loadTexture(MainActivity.getContext(),(filename + ".jpg"));

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
    public FloatBuffer getmNormalBuffer() {
        return mNormalBuffer;
    }
    @SuppressWarnings("WeakerAccess")
    public FloatBuffer getmTextureBuffer() {
        return mTextureBuffer;
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

        shaderProgram.render(this.getmVertexBuffer(), this.getmTextureBuffer(), this.getmNormalBuffer(), this.getmColorBuffer(), mTextureDataHandle, this.getmIndexBuffer());

    }

    /*
     * Sets the shader program used by this geometry.
     */
    public void setShaderProgram(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }
}
