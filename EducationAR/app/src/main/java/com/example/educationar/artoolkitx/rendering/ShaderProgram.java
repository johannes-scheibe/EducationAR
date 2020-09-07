/*
 *  ShaderProgram.java
 *  artoolkitX
 *
 *  This file is part of artoolkitX.
 *
 *  artoolkitX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  artoolkitX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with artoolkitX.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  As a special exception, the copyright holders of this library give you
 *  permission to link this library with independent modules to produce an
 *  executable, regardless of the license terms of these independent modules, and to
 *  copy and distribute the resulting executable under terms of your choice,
 *  provided that you also meet, for each linked independent module, the terms and
 *  conditions of the license of that module. An independent module is a module
 *  which is neither derived from nor based on this library. If you modify this
 *  library, you may extend this exception to your version of the library, but you
 *  are not obligated to do so. If you do not wish to do so, delete this exception
 *  statement from your version.
 *
 *  Copyright 2018 Realmax, Inc.
 *  Copyright 2015 Daqri, LLC.
 *  Copyright 2011-2015 ARToolworks, Inc.
 *
 *  Author(s): Philip Lamb, Thorsten Bux
 *
 */


package com.example.educationar.artoolkitx.rendering;

import android.opengl.GLES20;


import com.example.educationar.artoolkitx.rendering.OpenGLShader;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public abstract class ShaderProgram {

    /* Size of the position data in elements. */
    protected final int positionDataSize = 3;

    /* Size of the color data in elements. */
    protected final int colorDataSize = 4;

    /* Size of the texture data in elements. */
    protected final int textureDataSize = 2;

    /* Size of the normal data in elements. */
    protected final int normalDataSize = 3;

    /* How many bytes per float. */
    private final int mBytesPerFloat = Float.SIZE / 8;

    /* How many elements per vertex in bytes*/
    protected final int positionStrideBytes = positionDataSize * mBytesPerFloat;

    /* How many elements per vertex in bytes for the color*/
    protected final int colorStrideBytes = colorDataSize * mBytesPerFloat;

    /* How many elements per vertex in bytes for the the texture*/
    protected final int texCoordinateStrideBytes = textureDataSize * mBytesPerFloat;

    protected final int shaderProgramHandle;

    private float[] projectionMatrix;
    private float[] modelViewMatrix;

    @SuppressWarnings("WeakerAccess")
    public abstract int getProjectionMatrixHandle();

    @SuppressWarnings("WeakerAccess")
    public abstract int getModelViewMatrixHandle();

    protected abstract void bindAttributes();

    public ShaderProgram(OpenGLShader vertexShader, OpenGLShader fragmentShader) {
        shaderProgramHandle = createProgram(vertexShader.configureShader(), fragmentShader.configureShader());
    }

    // Linking a vertex and fragment shader together into a program
    private int createProgram(int vertexShaderHandle, int fragmentShaderHandle) {
        // Create a program object and store the handle to it.
        int programHandle = GLES20.glCreateProgram();
        String programErrorLog = "";

        if (programHandle != 0) {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0) {
                programErrorLog = GLES20.glGetProgramInfoLog(programHandle);
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0) {
            throw new RuntimeException("Error creating program.\\n " + programErrorLog);
        }
        return programHandle;
    }
    public int getShaderProgramHandle() {
        return shaderProgramHandle;
    }


    /**
     * Full loaded render function. You should at least override this one.
     * You need to set the projection and/or modelview matrix befor calling a render method.
     *
     * @param vertexBuffer position vertex information
     * @param colorBuffer  color information
     */
    public void render(FloatBuffer vertexBuffer, FloatBuffer textureBuffer, FloatBuffer normalBuffer, FloatBuffer colorBuffer, int textureDataHandle) {
        throw new RuntimeException("Please override at least this method.");
    }

    @SuppressWarnings("WeakerAccess")
    public void render(FloatBuffer vertexBuffer) {
        render(vertexBuffer, null, null, null, 0);
    }

    /**
     * Only render a simple position. In this case the implementation if forwarded to the
     * {@link #render(FloatBuffer)}
     *
     * @param position The position to be rendered
     */
    public void render(float[] position) {
        render(RenderUtils.buildFloatBuffer(position));
    }

    public void setProjectionMatrix(float[] projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }

    public void setModelViewMatrix(float[] modelViewMatrix) {
        this.modelViewMatrix = modelViewMatrix;
    }

    /**
     * Sets some basic settings for shader usage.
     * Needs to be called as first method from your implementation inside the
     * renderer() method.
     */
    protected void setupShaderUsage() {
        // Tell OpenGL to use this program when rendering.
        GLES20.glUseProgram(shaderProgramHandle);

        /* Replaces the functions
                Apply the artoolkitX projection matrix
                GLES10.glMatrixMode(GL10.GL_PROJECTION);
                GLES10.glLoadMatrixf(ARController.getInstance().getProjectionMatrix(), 0);
                gl.glMatrixMode(GL10.GL_MODELVIEW);
                gl.glLoadMatrixf(ARController.getInstance().queryTrackableTransformation(markerID), 0);
           from the Renderer implementation class in the render method
           */

        if (projectionMatrix != null)
            GLES20.glUniformMatrix4fv(this.getProjectionMatrixHandle(), 1, false, projectionMatrix, 0);
        else
            throw new RuntimeException("You need to set the projection matrix.");

        if (modelViewMatrix != null)
            GLES20.glUniformMatrix4fv(this.getModelViewMatrixHandle(), 1, false, modelViewMatrix, 0);
    }


}
