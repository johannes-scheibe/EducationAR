package com.example.educationar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;

import org.artoolkitx.arx.arxj.ARActivity;
import org.artoolkitx.arx.arxj.assets.AssetHelper;
import org.artoolkitx.arx.arxj.rendering.ARRenderer;

public class MainActivity extends ARActivity {

    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
