package com.example.educationar.ui.camera;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.educationar.R;
import com.example.educationar.artoolkitx.ARFragment;
import com.example.educationar.artoolkitx.rendering.ARRenderer;

public class ARCameraFragment extends ARFragment {

    private CameraViewModel cameraViewModel;
    private static Context mContext;

    private EducationARRenderer renderer;

    private TextView fpsLabel;

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

        renderer = new EducationARRenderer();

        renderer.getFps().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(@Nullable Float s) {
                fpsLabel = getFpsView();
                if(fpsLabel!=null)
                    fpsLabel.setText(cutFloat(s).toString());
            }
        });
        return root;
    }

    private Float cutFloat(Float fps){
        return (float)((int)( fps *100f ))/100f;
    }
    /**
     *  Passes the renderer used to create the ar content to artoolkitX
     */
    @Override
    protected ARRenderer supplyRenderer() {
        return renderer;
    }

    /**
     * Use the FrameLayout in this Activity's UI.
     */
    @Override
    protected FrameLayout supplyFrameLayout() {
        return (FrameLayout) getActivity().findViewById(R.id.mainFrameLayout);
    }
}
