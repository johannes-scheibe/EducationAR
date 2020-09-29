package com.example.educationar.ui.marker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.educationar.R;
import com.example.educationar.utils.FileManager;
import com.example.educationar.utils.MarkerGenerator;

public class MarkerFragment extends Fragment {

    private EditText idInput;

    private ImageView markerView;
    private Button saveButton;

    private int id;
    private Bitmap bm;

    private static final int ASK_PERMISSION = 1;
    private static final int SAVE_MARKER = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_marker, container, false);
        markerView = (ImageView) root.findViewById(R.id.markerPreview);
        idInput = (EditText) root.findViewById(R.id.markeridInput);

        saveButton = (Button) root.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/jpeg");

                    intent.putExtra(Intent.EXTRA_TITLE, "Marker-" + id + ".jpg");
                    startActivityForResult(intent, SAVE_MARKER);
                } else {
                    getActivity().requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ASK_PERMISSION);
                }
            }
        });
        saveButton.setVisibility(View.INVISIBLE);

        Button generateButton = (Button) root.findViewById(R.id.generateMarker);
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = idInput.getText().toString();
                if(!temp.equals("")){
                    id = Integer.parseInt(temp);
                    bm = MarkerGenerator.generateMarker(id,5);
                    markerView.setImageBitmap(bm);
                    saveButton.setVisibility(View.VISIBLE);
                }
            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode== SAVE_MARKER && resultCode == Activity.RESULT_OK) {
            FileManager.saveBitmap(getContext(), data.getData(), bm);
        }
    }
}
