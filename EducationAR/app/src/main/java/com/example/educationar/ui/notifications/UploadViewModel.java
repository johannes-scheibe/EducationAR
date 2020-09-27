package com.example.educationar.ui.notifications;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.OpenableColumns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.educationar.MainActivity;
import com.example.educationar.utils.FileManager;
import com.example.educationar.utils.MarkerGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UploadViewModel extends ViewModel {

    private Context mContext;

    private static Logger logger = Logger.getLogger("EduAR-UploadFragment");

    private MutableLiveData<Uri> modelUri;
    private MutableLiveData<Uri> textureUri;

    private MutableLiveData<String> modelName;

    private MutableLiveData<Bitmap> marker;
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
        marker = new MutableLiveData<>();
        markerID = new MutableLiveData<>();

        errorCodes = new MutableLiveData<>();
        errorCodes.setValue(new ArrayList<Integer>());
        int n = 10000;
        Random generator = new Random();
        markerID.setValue(generator.nextInt(n));
    }

    public void addModel(){
        if(requiredDataAvailable()){
            Random r = new Random();
            int id = r.nextInt(4000);
            Bitmap bm = MarkerGenerator.generateMarker(id, 5);
            marker.setValue(bm);

            try {
                FileManager.transferToInternalStorage(mContext, modelUri.getValue(), "random12323");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void saveMarker(Uri uri){
        FileManager.saveBitmap(mContext, uri, marker.getValue());
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

    public LiveData<Bitmap> getMarker(){
        return marker;
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