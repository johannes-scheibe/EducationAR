package com.example.educationar.ui.camera;




import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES20;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.educationar.MainActivity;
import com.example.educationar.Model;
import com.example.educationar.artoolkitx.FPSCounter;
import com.example.educationar.artoolkitx.rendering.ARRenderer;
import com.example.educationar.shader_impl.MyFragmentShader;
import com.example.educationar.shader_impl.MyShaderProgram;
import com.example.educationar.shader_impl.MyVertexShader;

import org.artoolkitx.arx.arxj.ARController;
import org.artoolkitx.arx.arxj.ARX_jni;

public class EducationARRenderer extends ARRenderer {

    private static Logger logger = Logger.getLogger("EduAR-ARRenderer");

    int ARW_TRACKER_OPTION_SQUARE_PATTERN_DETECTION_MODE = 4;
    int AR_MATRIX_CODE_DETECTION = 2;
    int ARW_TRACKER_OPTION_SQUARE_MATRIX_CODE_TYPE = 6;
    int AR_MATRIX_CODE_5x5_BCH_22_7_7  = 0x505;

    private MyShaderProgram shaderProgram;
    private FPSCounter fpsCounter;
    private float maxfps = 0;


    private List<Model> models;

    private Map<Integer, Model> trackables;


    //Shader calls should be within a GL thread. GL threads are onSurfaceChanged(), onSurfaceCreated() or onDrawFrame()
    //As the cube instantiates the shader during setShaderProgram call we need to create the cube here.
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        this.shaderProgram = new MyShaderProgram(new MyVertexShader(), new MyFragmentShader());
        this.fpsCounter = new FPSCounter();
        this.models = new ArrayList<Model>();



        Model sphere = new Model(MainActivity.getContext(), "Models/sphere/sphere");
        sphere.setShaderProgram(shaderProgram);
        this.models.add(sphere);

        Model cube = new Model(MainActivity.getContext(), "Models/cube/cube");
        cube.setShaderProgram(shaderProgram);
        this.models.add(cube);

        Model monkey = new Model(MainActivity.getContext(), "Models/monkey/monkey");
        monkey.setShaderProgram(shaderProgram);
        this.models.add(monkey);


        super.onSurfaceCreated(unused, config);

    }

    /**
     * Markers can be configured here.
     */
    @Override
    public boolean configureARScene() {

        trackables = new HashMap<Integer, Model>();

        // Add the Markers
        for(int i = 0; i<models.size(); i++) {
            trackables.put(ARController.getInstance().addTrackable("single_barcode;" + i + ";80"), models.get(i));
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
        fpsCounter.frame();
        if(maxfps<fpsCounter.getFPS()){
            maxfps= fpsCounter.getFPS();
        }
        logger.log(Level.INFO, "FPS: " + maxfps);

        // Initialize GL
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glFrontFace(GLES20.GL_CCW);
        
        // Look for trackables, and draw on each found one.
        for (int trackableUID : trackables.keySet()) {
            // If the trackable is visible, apply its transformation, and render the object
            float[] modelViewMatrix = new float[16];
            if (ARController.getInstance().queryTrackableVisibilityAndTransformation(trackableUID, modelViewMatrix)) {
                float[] projectionMatrix = ARController.getInstance().getProjectionMatrix(10.0f, 10000.0f);
                trackables.get(trackableUID).draw(projectionMatrix, modelViewMatrix);
            }
        }
    }
}