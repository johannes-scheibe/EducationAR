package com.example.educationar.ui.administration.upload;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.Navigation;

import com.example.educationar.MainActivity;
import com.example.educationar.R;
import com.example.educationar.utils.FileManager;
import com.example.educationar.utils.ModelManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UploadViewModel extends ViewModel {

    private Context mContext;

    private static Logger logger = Logger.getLogger("EduAR-UploadFragment");

    private MutableLiveData<Uri> modelUri, textureUri;

    private MutableLiveData<String> modelName;

    private MutableLiveData<Integer> markerID;

    private MutableLiveData<List<Integer>> errorCodes;

    private static final Integer ERROR_ID_NAME = 0;
    private static final Integer ERROR_ID_MODEL = 1;
    private static final Integer ERROR_ID_TEXTURE = 2;

    public UploadViewModel() {
        mContext = MainActivity.getContext();

        modelUri = new MutableLiveData<>();
        textureUri = new MutableLiveData<>();
        modelName = new MutableLiveData<>();
        markerID = new MutableLiveData<>();

        errorCodes = new MutableLiveData<>();
        errorCodes.setValue(new ArrayList<Integer>());
        markerID.setValue(ModelManager.getNextID(mContext));
    }

    public void addModel(View v){
        if(requiredDataAvailable()){

            final String displayName = modelName.getValue();
            final String modelFileName = ModelManager.getFileName(markerID.getValue());
            final String textureFileName = modelFileName + "-texture";
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        ModelManager.getInstance().addModel(markerID.getValue(), displayName);
                        FileManager.transferToInternalStorage(mContext, modelUri.getValue(), modelFileName);
                        FileManager.transferToInternalStorage(mContext, textureUri.getValue(), textureFileName);
                        ModelManager.getInstance().loadModel(markerID.getValue());
                        logger.log(Level.INFO, "Successfully added the model to internal storage.");
                        MainActivity.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                CharSequence text = "Dein Modell '" + displayName + "' wurde erfolgreich hinzugefügt!";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(mContext, text, duration);
                                toast.show();
                            }
                        });


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }).start();



            Bundle bundle = new Bundle();
            bundle.putString("name", modelName.getValue());
            bundle.putInt("id", markerID.getValue());
            Navigation.findNavController(v).navigate(R.id.action_upload_to_confiramation, bundle);
        }
    }

    private boolean requiredDataAvailable(){
        boolean available = true;
        List<Integer> list = new ArrayList<>();

        logger.log(Level.INFO, modelName.getValue());
        if (modelName.getValue() == ""){
            addIfNotContains(list, ERROR_ID_NAME);
            available = false;
        }
        if (modelUri.getValue()==null){
            addIfNotContains(list, ERROR_ID_MODEL);
            available = false;
        }
        if (textureUri.getValue()==null){
            addIfNotContains(list, ERROR_ID_TEXTURE);
            available = false;
        }
        errorCodes.setValue(list);
        return available;
    }

    private boolean correctFileType(Uri uri, String ending){
        String fname = getFileName(uri);
        if(fname.endsWith(ending)){
            return true;
        }

        List err = errorCodes.getValue();
        addIfNotContains(err, ERROR_ID_MODEL);
        errorCodes.setValue(err);

        CharSequence text = "Falscher Dateityp!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(mContext, text, duration);
        toast.show();


        return false;
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void addIfNotContains(List<Integer> list, int i ) {
        if (!list.contains(i))
            list.add(i);
    }


    // Getter & Setter

    public void setModel(Uri uri){
        if (correctFileType(uri, ".obj"))
            modelUri.setValue(uri);
    }

    public void setTexture(Uri uri){
        textureUri.setValue(uri);
    }
    public void setName(String name){
        modelName.setValue(name);
    }


    public LiveData<Integer> getMarkerID(){
        return markerID;
    }

    public LiveData<Uri> getModelUri(){
        return modelUri;
    }

    public LiveData<Uri> getTextureUri(){
        return textureUri;
    }

    public LiveData<String> getName(){
        return modelName;
    }

    public LiveData<List<Integer>> getErrorCodes(){
        return errorCodes;
    }
}