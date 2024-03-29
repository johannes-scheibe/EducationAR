package com.example.educationar.ui.camera;




import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.widget.TextView;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.educationar.MainActivity;
import com.example.educationar.utils.Model;
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
    private MutableLiveData<Float> fps = new MutableLiveData<>();

    private TextView fpsLabel;

    private Map<Integer, Model> models;
    private Map<Integer, Integer> trackables;

    float[] mViewMatrix = new float[16];
    float[] mModelMatrix = new float[16];



    //Shader calls should be within a GL thread. GL threads are onSurfaceChanged(), onSurfaceCreated() or onDrawFrame()
    //As the cube instantiates the shader during setShaderProgram call we need to create the cube here.
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        this.shaderProgram = new MyShaderProgram(new MyVertexShader(), new MyFragmentShader());
        this.fpsCounter = new FPSCounter();

        models = new HashMap<Integer, Model>();

        trackables = new HashMap<Integer, Integer>();

        mContext = MainActivity.getContext();

        models = ModelManager.getInstance().getModels();

        // Add the markers for the models
        for(Integer key : models.keySet()){
            trackables.put(ARController.getInstance().addTrackable("single_barcode;" + key + ";20"), key);
        }

        super.onSurfaceCreated(unused, config);

    }

    @Override
    public void onSurfaceChanged(GL10 unused, int w, int h) {
        super.onSurfaceChanged(unused, w, h);

    }

    /**
     * Markers can be configured here.
     */
    @Override
    public boolean configureARScene() {

        /* Code used for testing the MarkerGenerator class
        trackables.put(ARController.getInstance().addTrackable("single_barcode;0;40"), 0);
        trackables.put(ARController.getInstance().addTrackable("single_barcode;4194303;40"), 4194303);
        trackables.put(ARController.getInstance().addTrackable("single_barcode;428368;40"), 428638);
        trackables.put(ARController.getInstance().addTrackable("single_barcode;3604631;40"), 3604631);
        trackables.put(ARController.getInstance().addTrackable("single_barcode;1547643;40"), 1547643);
        */

        // Configure the tracking
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
        fps.postValue(fpsCounter.getFPS());


        // Initialize GL
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glFrontFace(GLES20.GL_CCW);

        float[] mvpMatrix = new float[16];
        float[] modelViewMatrix = new float[16];


        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 1.0f, 0.0f, 0.0f);

        // Combine the model matrix with the projection and camera view
        Matrix.multiplyMM(modelViewMatrix, 0, mModelMatrix, 0, mViewMatrix,0);

        // Look for trackables, and draw on each found one.
        for (int trackableUID : trackables.keySet()) {
            // If the trackable is visible, apply its transformation, and render the object
            if (ARController.getInstance().queryTrackableVisibilityAndTransformation(trackableUID, modelViewMatrix)) {
                float[] projectionMatrix = ARController.getInstance().getProjectionMatrix(10.0f, 10000.0f);
                int id = trackables.get(trackableUID);

                Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix,0, modelViewMatrix,0);

                models.get(id).draw(mvpMatrix);
                logger.log(Level.INFO, "Marker with ID: " + id + " tracked.");
            }
        }
    }

    public LiveData<Float> getFps(){
        return fps;
    }
}