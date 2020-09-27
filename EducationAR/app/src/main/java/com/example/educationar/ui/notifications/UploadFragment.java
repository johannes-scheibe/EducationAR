package com.example.educationar.ui.notifications;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.educationar.R;

import java.util.List;
import java.util.logging.Logger;

public class UploadFragment extends Fragment {

    private UploadViewModel uploadViewModel;

    private static Logger logger = Logger.getLogger("EduAR-UploadFragment");

    private EditText nameInput;
    private TextView modelPath;
    private TextView texturePath;
    private ImageView markerView;
    private Button saveButton;

    private static final int ASK_PERMISSION = 0;
    private static final int PICK_MODEL = 1;
    private static final int PICK_TEXTURE = 2;
    private static final int SAVE_MARKER = 3;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        uploadViewModel =
                ViewModelProviders.of(this).get(UploadViewModel.class);

        View root = inflater.inflate(R.layout.fragment_add_model, container, false);

        nameInput = root.findViewById(R.id.nameInput);
        modelPath = root.findViewById(R.id.modelPath);
        texturePath = root.findViewById(R.id.texturePath);
        markerView = root.findViewById(R.id.markerPreview);
        saveButton = root.findViewById(R.id.saveButton);

        saveButton.setVisibility(View.INVISIBLE);

        // Observer
        uploadViewModel.getModelUri().observe(getViewLifecycleOwner(), new Observer<Uri>() {
            @Override
            public void onChanged(@Nullable Uri s) {
                modelPath.setText(s.getPath());
            }
        });
        uploadViewModel.getTextureUri().observe(getViewLifecycleOwner(), new Observer<Uri>() {
            @Override
            public void onChanged(@Nullable Uri s) {
                texturePath.setText(s.getPath());
            }
        });
        uploadViewModel.getMarker().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(@Nullable Bitmap bm) {
                markerView.setImageBitmap(bm);
                saveButton.setVisibility(View.VISIBLE);
            }
        });
        uploadViewModel.getErrorCodes().observe(getViewLifecycleOwner(), new Observer<List<Integer>>() {
            @Override
            public void onChanged(@Nullable List<Integer> list) {
                nameInput.setError(null);
                modelPath.setError(null);
                texturePath.setError(null);
                for (Integer i : list) {
                    switch (i){
                        case 0:
                            nameInput.setError("");
                            break;
                        case 1:
                            modelPath.setError("");
                            break;
                        case 2:
                            texturePath.setError("");
                            break;
                    }
                }
            }
        });

        // OnChange listener
        nameInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                uploadViewModel.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                uploadViewModel.setName(s.toString());
            }
        });
        // Click listeners
        Button selectModel = (Button) root.findViewById(R.id.selectModel);
        selectModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, PICK_MODEL);
            }
        });

        Button selectTexture = (Button) root.findViewById(R.id.selectTexture);
        selectTexture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/jpeg");
                startActivityForResult(intent, PICK_TEXTURE);
            }
        });

        Button addModel = (Button) root.findViewById(R.id.addButton);
        addModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadViewModel.addModel();

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (ActivityCompat.checkSelfPermission(getContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Log.v(TAG,"Permission is granted");
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/jpeg");
                String fname = uploadViewModel.getName().getValue();
                intent.putExtra(Intent.EXTRA_TITLE, fname);
                startActivityForResult(intent, SAVE_MARKER);
            }else {
                getActivity().requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, ASK_PERMISSION);
            }
            }
        });


        // Set default name
        String name = "Model-" + uploadViewModel.getMarkerID().getValue();
        nameInput.setText(name);
        return root;
    }

    // Call Back method  to get the Message form other Activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        // check if the request code is same as what is passed
        if(requestCode==PICK_MODEL && resultCode == Activity.RESULT_OK)
        {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                uploadViewModel.setModel(uri);
            }
        }

        if(requestCode==PICK_TEXTURE && resultCode == Activity.RESULT_OK)
        {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                uploadViewModel.setTexture(uri);
            }
        }
        if(requestCode== SAVE_MARKER && resultCode == Activity.RESULT_OK) {
            uploadViewModel.saveMarker(data.getData());
        }
    }



}


