package com.example.educationar.ui.administration;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.educationar.MainActivity;
import com.example.educationar.R;
import com.example.educationar.artoolkitx.AndroidUtils;
import com.example.educationar.ui.upload.UploadFragment;
import com.example.educationar.utils.ModelManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AdministrationFragment extends Fragment {

    private static Logger logger = Logger.getLogger("EduAR-AdministrationFragment");

    private Context mContext;

    private AdministrationViewModel administrationViewModel;

    private SharedPreferences sharedPrefs;
    private LinearLayout verticalLayout;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mContext = MainActivity.getContext();

        administrationViewModel =
                ViewModelProviders.of(this).get(AdministrationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_administration, container, false);
        verticalLayout = (LinearLayout) root.findViewById(R.id.linearLayout);

        ImageButton addBtn = (ImageButton) root.findViewById(R.id.addButton);
        addBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_administration_to_upload, null));

        //Load all items from prefs
        Map<String,?> keys = ModelManager.getInstance().getModelReferences();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            LinearLayout l = createNewElem(Integer.parseInt(entry.getKey().trim()),  entry.getValue().toString());
            verticalLayout.addView(l);
        }

        return root;
    }



    private LinearLayout createNewElem(final int id, final String name){
        int width = getScreenWidth() - 30;

        LinearLayout container = new LinearLayout(mContext);
        container.setPadding(15,5,15,5);
        container.setGravity(Gravity.CENTER_HORIZONTAL);

        Switch sw = new Switch(mContext);
        sw.setLayoutParams(new ViewGroup.LayoutParams((int) (width*0.15), ViewGroup.LayoutParams.MATCH_PARENT));
        sw.setTextOn("start");
        sw.setTextOff("close");
        container.addView(sw);

        TextView t1 = new TextView(mContext);
        t1.setText(""+ id);
        t1.setTextSize(18);
        t1.setGravity(Gravity.CENTER);
        t1.setLayoutParams(new ViewGroup.LayoutParams((int) (width*0.15), ViewGroup.LayoutParams.MATCH_PARENT));
        container.addView(t1);

        TextView t2 = new TextView(mContext);
        t2.setText(name);
        t2.setTextSize(18);
        t2.setGravity(Gravity.CENTER_VERTICAL);
        t2.setLayoutParams(new ViewGroup.LayoutParams((int) (width*0.5), ViewGroup.LayoutParams.MATCH_PARENT));
        container.addView(t2);

        ImageButton btn = new ImageButton(mContext);
        btn.setImageResource(R.drawable.ic_delete_black_24dp);
        btn.setLayoutParams(new ViewGroup.LayoutParams((int) (width*0.2), ViewGroup.LayoutParams.WRAP_CONTENT));
        container.addView(btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logger.log(Level.INFO, "Delete Model " + name);
                ModelManager.getInstance().deleteModel(mContext, id, name);
                Navigation.findNavController(v).navigate(R.id.action_reload_administration, null);
            }
        });

        return container;
    }
    public int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
}
