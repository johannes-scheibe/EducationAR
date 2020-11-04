package com.example.educationar.utils;

import android.content.Context;

import com.example.educationar.artoolkitx.rendering.RenderUtils;
import com.example.educationar.artoolkitx.rendering.ShaderProgram;
import com.example.educationar.utils.ObjLoader;
import com.example.educationar.utils.TextureLoader;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.logging.Logger;

public class Model {

    private static Logger logger = Logger.getLogger("EduAR-Model");

    private Context mContext;

    private File mTextureFile;

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mColorBuffer;
    private FloatBuffer mNormalBuffer;
    private FloatBuffer mTextureBuffer;

    private ShaderProgram shaderProgram;

    // Load the Texture
    private int mTextureDataHandle;

    public Model(Context context, File modelFile, File textureFile) {

        mContext = context;

        mTextureFile = textureFile;

        ObjLoader objLoader = new ObjLoader(context, modelFile);

        mVertexBuffer = RenderUtils.buildFloatBuffer(objLoader.vertices);
        mColorBuffer = RenderUtils.buildFloatBuffer(objLoader.colors);
        mNormalBuffer = RenderUtils.buildFloatBuffer(objLoader.normals);
        mTextureBuffer = RenderUtils.buildFloatBuffer(objLoader.textures);

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

    /*
     * Used to render objects when working with OpenGL ES 2.x
     *
     * @param projectionMatrix The projection matrix obtained from the ARToolkit
     * @param modelViewMatrix  The marker transformation matrix obtained from ARToolkit
     */
    public void draw(float[] mvpMartix) {
        shaderProgram.setMvpMatrix(mvpMartix);
        shaderProgram.render(this.getmVertexBuffer(), this.getmTextureBuffer(), this.getmNormalBuffer(), this.getmColorBuffer(), mTextureDataHandle);
    }

    public void initialise(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
        mTextureDataHandle = TextureLoader.loadTexture(mContext, mTextureFile);
    }

    /*
     * Sets the shader program used by this geometry.
     */
    public void setShaderProgram(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }
}
