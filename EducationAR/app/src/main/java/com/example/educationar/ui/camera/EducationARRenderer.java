package com.example.educationar.ui.camera;




import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.educationar.MainActivity;
import com.example.educationar.Model;
import com.example.educationar.artoolkitx.FPSCounter;
import com.example.educationar.artoolkitx.rendering.ARRenderer;
import com.example.educationar.shader_impl.MyFragmentShader;
import com.example.educationar.shader_impl.MyShaderProgram;
import com.example.educationar.shader_impl.MyVertexShader;
import com.example.educationar.utils.ModelManager;

import org.artoolkitx.arx.arxj.ARController;
import org.artoolkitx.arx.arxj.ARX_jni;

public class EducationARRenderer extends ARRenderer {

    private static Logger logger = Logger.getLogger("EduAR-ARRenderer");

    private Context mContext;

    int ARW_TRACKER_OPTION_SQUARE_PATTERN_DETECTION_MODE = 4;
    int AR_MATRIX_CODE_DETECTION = 2;
    int ARW_TRACKER_OPTION_SQUARE_MATRIX_CODE_TYPE = 6;
    int AR_MATRIX_CODE_5x5_BCH_22_7_7  = 0x505;
    int AR_MATRIX_CODE_5x5 = 0x05;

    private MyShaderProgram shaderProgram;
    private FPSCounter fpsCounter;
    private float maxfps = 0;



    private Map<Integer, Model> models;
    private Map<Integer, Model> trackables;

    //Shader calls should be within a GL thread. GL threads are onSurfaceChanged(), onSurfaceCreated() or onDrawFrame()
    //As the cube instantiates the shader during setShaderProgram call we need to create the cube here.
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        this.shaderProgram = new MyShaderProgram(new MyVertexShader(), new MyFragmentShader());
        this.fpsCounter = new FPSCounter();

        models = new HashMap<Integer, Model>();
        trackables = new HashMap<Integer, Model>();

        mContext = MainActivity.getContext();

        File dir = mContext.getFilesDir();

        Map<String,?> models = ModelManager.getAllModels();
        for(Map.Entry<String,?> entry : models.entrySet()){
            File modelFile = new File(dir, entry.getValue().toString());
            File textureFile = new File(dir, entry.getValue().toString()+"-texture");
            Model model = new Model(mContext, modelFile, textureFile);
            model.setShaderProgram(shaderProgram);
            this.models.put(Integer.parseInt(entry.getKey().trim()), model);
        }



        super.onSurfaceCreated(unused, config);

    }

    /**
     * Markers can be configured here.
     */
    @Override
    public boolean configureARScene() {



        // Add the Markers

        for(Map.Entry<Integer, Model> entry : models.entrySet()){
            trackables.put(ARController.getInstance().addTrackable("single_barcode;" + entry.getKey() + ";80"), entry.getValue());
        }
        ARX_jni.arwSetTrackerOptionInt(ARW_TRACKER_OPTION_SQUARE_PATTERN_DETECTION_MODE, AR_MATRIX_CODE_DETECTION);
        ARX_jni.arwSetTrackerOptionInt(ARW_TRACKER_OPTION_SQUARE_MATRIX_CODE_TYPE, AR_MATRIX_CODE_5x5);
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