package com.example.educationar.ui.administration.confirmation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.educationar.R;
import com.example.educationar.utils.FileManager;
import com.example.educationar.utils.MarkerGenerator;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfirmationFragment extends Fragment {

    private static Logger logger = Logger.getLogger("EduAR-MarkerGenerator");

    private static final String ARG_NAME = "name";
    private static final String ARG_ID = "id";

    private String mName;
    private int mID;
    private Bitmap bm;

    private static final int ASK_PERMISSION = 0;
    private static final int SAVE_MARKER = 1;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(getView()).navigate(R.id.action_confirmation_to_administration, null);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        // The callback can be enabled or disabled here or in handleOnBackPressed()
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_confirmation, container, false);


        mName = getArguments().getString(ARG_NAME);
        mID = getArguments().getInt(ARG_ID);

        logger.log(Level.INFO, mName + "    " + mID);


        bm = MarkerGenerator.generateMarker(mID, 5);

        ImageView markerView = root.findViewById(R.id.markerPreview);
        markerView.setImageBitmap(bm);

        Button saveButton = (Button) root.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/jpeg");

                    intent.putExtra(Intent.EXTRA_TITLE, "Marker-" + mName + ".jpg");
                    startActivityForResult(intent, SAVE_MARKER);
                } else {
                    getActivity().requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ASK_PERMISSION);
                }
            }
        });

        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ASK_PERMISSION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/jpeg");

                    intent.putExtra(Intent.EXTRA_TITLE, mName);
                    startActivityForResult(intent, SAVE_MARKER);
                }
                return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode== SAVE_MARKER && resultCode == Activity.RESULT_OK) {
            FileManager.saveBitmap(getContext(), data.getData(), bm);
            Navigation.findNavController(getView()).navigate(R.id.action_confirmation_to_administration, null);
        }
    }

}
