/*
 *  SimpleShaderProgram.java
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
 *  Author(s): Thorsten Bux
 *
 */
package com.example.educationar.shader_impl;

import android.opengl.GLES20;


import com.example.educationar.artoolkitx.rendering.OpenGLShader;
import com.example.educationar.artoolkitx.rendering.ShaderProgram;

import java.nio.FloatBuffer;

/**
 * The shader program links together the vertex shader and the fragment shader and compiles them.
 * It also is responsible for binding the attributes. Attributes can be used to pass in values to the
 * shader during runtime.
 * <p/>
 * Finally it renders the given geometry.
 */
public class MyShaderProgram extends ShaderProgram {

    public MyShaderProgram(OpenGLShader vertexShader, OpenGLShader fragmentShader) {
        super(vertexShader, fragmentShader);
        bindAttributes();
    }

    protected void bindAttributes() {
        // Bind attributes
        GLES20.glBindAttribLocation(shaderProgramHandle, 0, OpenGLShader.positionVectorString);
    }

    @Override
    public int getMVPMatrixHandle() {
        return GLES20.glGetUniformLocation(shaderProgramHandle, OpenGLShader.mvpMatrixString);
    }

    public int getTextureUniformHandle() {
        return GLES20.glGetUniformLocation(shaderProgramHandle, OpenGLShader.textureString);
    }

    @SuppressWarnings("WeakerAccess")
    public int getPositionHandle() {
        return GLES20.glGetAttribLocation(shaderProgramHandle, OpenGLShader.positionVectorString);
    }

    /**
     * @return The handle for the color of the geometry. Used later to pass in the color of
     * the geometry.
     */
    private int getColorHandle() {
        return GLES20.glGetAttribLocation(shaderProgramHandle, MyVertexShader.colorVectorString);
    }

    private int getTextureCoordinateHandle() {
        return GLES20.glGetAttribLocation(shaderProgramHandle, MyVertexShader.textureVectorString);
    }
    private int getNormalHandle() {
        return GLES20.glGetAttribLocation(shaderProgramHandle, MyVertexShader.normalVectorString);
    }

    /**
     * There are several render methods available from the base class. In this case we override the {@link ShaderProgram#render(FloatBuffer, FloatBuffer, FloatBuffer, FloatBuffer, int)} one.
     * Although we never use the index ByteBuffer.
     * We pass in the vertex and color information from the  object.
     *
     * @param vertexBuffer Contains the position information as two vertexes. Start and end of the line to draw
     * @param colorBuffer  Contains the color of the line
     */

    @Override
    public void render(FloatBuffer vertexBuffer, FloatBuffer textureBuffer, FloatBuffer normalBuffer, FloatBuffer colorBuffer, int textureDataHandle) {
        setupShaderUsage();

        // Pass in the vertex coordinate information
        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(this.getPositionHandle(), positionDataSize, GLES20.GL_FLOAT, false,
                positionStrideBytes, vertexBuffer);
        GLES20.glEnableVertexAttribArray(this.getPositionHandle());

        // Pass in the color information
        if(colorBuffer != null) {
            colorBuffer.position(0);
            GLES20.glVertexAttribPointer(this.getColorHandle(), colorDataSize, GLES20.GL_FLOAT, false,
                    colorStrideBytes, colorBuffer);
            GLES20.glEnableVertexAttribArray(this.getColorHandle());
        }

        // Pass in the normal information
        if(normalBuffer != null) {
            normalBuffer.position(0);
            GLES20.glVertexAttribPointer(this.getNormalHandle(), normalDataSize, GLES20.GL_FLOAT, false,
                    0, normalBuffer);
            GLES20.glEnableVertexAttribArray(this.getNormalHandle());
        }

        // Pass in the texture coordinate information
        if(textureBuffer != null) {
            // Set the active texture unit to texture unit 0.
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

            // Bind the texture to this unit.
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle);

            // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
            GLES20.glUniform1i(this.getTextureUniformHandle(), 0);

            textureBuffer.position(0);
            GLES20.glVertexAttribPointer(this.getTextureCoordinateHandle(), textureDataSize, GLES20.GL_FLOAT, false,
                    texCoordinateStrideBytes, textureBuffer);
            GLES20.glEnableVertexAttribArray(this.getTextureCoordinateHandle());
        }

        vertexBuffer.position(0);

        int numVertices =  (vertexBuffer.limit()/positionDataSize);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, numVertices);
    }

}