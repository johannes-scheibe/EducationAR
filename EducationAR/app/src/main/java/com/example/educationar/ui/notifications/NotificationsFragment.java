package com.example.educationar.ui.notifications;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.educationar.R;
import com.example.educationar.artoolkitx.ARActivity;
import com.example.educationar.utils.MarkerGenerator;

import java.io.OutputStream;
import java.util.Random;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;

    private TextView modelPath;
    private TextView texturePath;
    private ImageView markerView;
    private Button saveMarker;

    private static final int ASK_PERMISSION = 0;
    private static final int PICK_MODEL = 1;
    private static final int PICK_TEXTURE = 2;
    private static final int CHOOSE_FOLDER = 3;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);

        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        View root = inflater.inflate(R.layout.fragment_add_model, container, false);

        modelPath = root.findViewById(R.id.modelPath);
        texturePath = root.findViewById(R.id.texturePath);
        markerView = root.findViewById(R.id.markerPreview);
        saveMarker = root.findViewById(R.id.saveMarker);

        saveMarker.setVisibility(View.INVISIBLE);

        // Click Listeners
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

        Button addModel = (Button) root.findViewById(R.id.addModel);
        addModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random();
                int id = r.nextInt(4000);
                Bitmap bm = MarkerGenerator.generateMarker(id,5);
                markerView.setImageBitmap(bm);
                saveMarker.setVisibility(View.VISIBLE);
            }
        });

        saveMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    //Log.v(TAG,"Permission is granted");
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    startActivityForResult(intent, CHOOSE_FOLDER);
                }else {
                    getActivity().requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, ARActivity.REQUEST_CAMERA_PERMISSION_RESULT);

                }
            }
        });

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
                modelPath.setText(uri.getPath());
            }
        }

        if(requestCode==PICK_TEXTURE && resultCode == Activity.RESULT_OK)
        {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                texturePath.setText(uri.getPath());
            }
        }
        if(requestCode==CHOOSE_FOLDER && resultCode == Activity.RESULT_OK)
        {

            BitmapDrawable drawable = (BitmapDrawable) markerView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            Uri treeUri = data.getData();

            DocumentFile pickedDir = DocumentFile.fromTreeUri(getContext(), treeUri);

            // List all existing files inside picked directory
            for (DocumentFile file : pickedDir.listFiles()) {
                //Log.d(TAG, "Found file " + file.getName() + " with size " + file.length());
            }

            getActivity().getContentResolver().takePersistableUriPermission(treeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION |
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            // Create a new file and write into it
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fname = "Image-"+ n +".jpg";
            DocumentFile  newFile = pickedDir.createFile("image/jpeg", fname);
            OutputStream out = null;
            try {
                out = getActivity().getContentResolver().openOutputStream(newFile.getUri());
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.write("A long time ago...".getBytes());
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
