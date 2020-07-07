package com.example.educationar;

import org.artoolkitx.arx.arxj.ARController;
import org.artoolkitx.arx.arxj.ARX_jni;
import org.artoolkitx.arx.arxj.Trackable;
import org.artoolkitx.arx.arxj.rendering.ARRenderer;
import org.artoolkitx.arx.arxj.rendering.shader_impl.Cube;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES20;



import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.example.educationar.rendering.MyFragmentShader;
import com.example.educationar.rendering.MyShaderProgram;
import com.example.educationar.rendering.MyVertexShader;

public class EducationARRenderer extends ARRenderer {

    private static Logger logger = Logger.getLogger("EduAR-ARRenderer");

    int ARW_TRACKER_OPTION_SQUARE_PATTERN_DETECTION_MODE = 4;
    int AR_MATRIX_CODE_DETECTION = 2;
    int ARW_TRACKER_OPTION_SQUARE_MATRIX_CODE_TYPE = 6;
    int AR_MATRIX_CODE_5x5_BCH_22_7_7  = 0x505;

    private MyShaderProgram shaderProgram;


    private List<Model> models = new ArrayList<Model>();


    //Shader calls should be within a GL thread. GL threads are onSurfaceChanged(), onSurfaceCreated() or onDrawFrame()
    //As the cube instantiates the shader during setShaderProgram call we need to create the cube here.
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        this.shaderProgram = new MyShaderProgram(new MyVertexShader(), new MyFragmentShader());

        Model sphere = new Model(EducationARApplication.getContext(), "Models/sphere/sphere");
        sphere.setShaderProgram(shaderProgram);
        models.add(sphere);

        Model cube = new Model(EducationARApplication.getContext(), "Models/cube/cube");
        cube.setShaderProgram(shaderProgram);
        models.add(cube);

        Model monkey = new Model(EducationARApplication.getContext(), "Models/monkey/monkey");
        monkey.setShaderProgram(shaderProgram);
        models.add(monkey);

        super.onSurfaceCreated(unused, config);

    }

    /**
     * Markers can be configured here.
     */
    @Override
    public boolean configureARScene() {

        // Add the Markers
        for(int i = 0; i<models.size(); i++) {
            ARController.getInstance().addTrackable("single_barcode;" + i + ";80");

        }

        ARX_jni.arwSetTrackerOptionInt(ARW_TRACKER_OPTION_SQUARE_PATTERN_DETECTION_MODE, AR_MATRIX_CODE_DETECTION);
        ARX_jni.arwSetTrackerOptionInt(ARW_TRACKER_OPTION_SQUARE_MATRIX_CODE_TYPE, AR_MATRIX_CODE_5x5_BCH_22_7_7);
        return true;
    }

    /**
     * Override the draw function from ARRenderer.
     */
    @Override
    public void draw() {
        super.draw();

        // Initialize GL
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glFrontFace(GLES20.GL_CCW);
        
        // Look for trackables, and draw on each found one.
        for (int trackableUID = 0; trackableUID<models.size(); trackableUID++) {
            // If the trackable is visible, apply its transformation, and render a cube
            float[] modelViewMatrix = new float[16];
            if (ARController.getInstance().queryTrackableVisibilityAndTransformation(trackableUID, modelViewMatrix)) {
                float[] projectionMatrix = ARController.getInstance().getProjectionMatrix(10.0f, 10000.0f);
                models.get(trackableUID).draw(projectionMatrix, modelViewMatrix);
            }
        }
    }
}