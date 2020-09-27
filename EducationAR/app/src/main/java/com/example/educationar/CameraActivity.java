package com.example.educationar;

import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.educationar.artoolkitx.ARActivity;
import com.example.educationar.artoolkitx.rendering.ARRenderer;
import com.example.educationar.ui.camera.EducationARRenderer;

public class CameraActivity extends ARActivity {

    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_camera);

        this.mContext = getApplicationContext();
    }

    /**
     *  Passes the renderer used to create the ar content to artoolkitX
     */
    @Override
    protected ARRenderer supplyRenderer() {
        return new EducationARRenderer();
    }

    /**
     * Use the FrameLayout in this Activity's UI.
     */
    @Override
    protected FrameLayout supplyFrameLayout() {
        return (FrameLayout) this.findViewById(R.id.mainFrameLayout);
    }
}
