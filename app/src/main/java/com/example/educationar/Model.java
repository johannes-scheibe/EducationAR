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

    public Model(Context context,  String file, float[] colors) {

        MyObjLoader objLoader = new MyObjLoader( file);


        mVertexBuffer = RenderUtils.buildFloatBuffer(objLoader.vertices);
        if(colors !=null){
            mColorBuffer = RenderUtils.buildFloatBuffer(colors);
        }else{
            mColorBuffer=null;
        }

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
