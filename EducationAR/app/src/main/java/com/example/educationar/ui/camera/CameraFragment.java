package com.example.educationar.ui.camera;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import com.example.educationar.R;
import com.example.educationar.artoolkitx.ARFragment;
import com.example.educationar.artoolkitx.rendering.ARRenderer;

public class CameraFragment extends ARFragment {

    private CameraViewModel cameraViewModel;
    private static Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mContext = getActivity();


    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cameraViewModel =
                ViewModelProviders.of(this).get(CameraViewModel.class);
        View root = inflater.inflate(R.layout.fragment_camera, container, false);

        return root;
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
        return (FrameLayout) getActivity().findViewById(R.id.mainFrameLayout);
    }
}
