package com.example.educationar.ui.marker;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.educationar.R;
import com.example.educationar.utils.MarkerGenerator;


public class MarkerFragment extends Fragment {

    private MarkerViewModel markerViewModel;
    private EditText idInput;

    private ImageView markerView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        markerViewModel =
                ViewModelProviders.of(this).get(MarkerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_marker, container, false);
        markerView = (ImageView) root.findViewById(R.id.markerView);
        idInput = (EditText) root.findViewById(R.id.markeridInput);

        Button generateButton = (Button) root.findViewById(R.id.generateMarker);
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = idInput.getText().toString();
                if(!temp.equals("")){
                    int id = Integer.parseInt(temp);
                    Bitmap bm = MarkerGenerator.generateMarker(id,5);
                    markerView.setImageBitmap(bm);
                }
            }
        });
        return root;
    }
}
